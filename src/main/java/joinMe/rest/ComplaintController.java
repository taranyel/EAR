package joinMe.rest;

import jakarta.validation.Valid;
import joinMe.db.entity.Complaint;
import joinMe.db.entity.User;
import joinMe.rest.dto.ComplaintDTO;
import joinMe.rest.dto.Mapper;
import joinMe.rest.util.RestUtils;
import joinMe.service.ComplaintService;
import joinMe.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/complaints")
public class ComplaintController {
    private static final Logger LOG = LoggerFactory.getLogger(ComplaintController.class);

    private final ComplaintService complaintService;

    private final UserService userService;

    private final Mapper mapper;

    @Autowired
    public ComplaintController(ComplaintService complaintService, UserService userService, Mapper mapper) {
        this.complaintService = complaintService;
        this.userService = userService;
        this.mapper = mapper;
    }

    @PreAuthorize("!anonymous")
    @PostMapping(value = "/{accusedID}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createComplaint(@PathVariable Integer accusedID, @Valid @RequestBody ComplaintDTO complaintDTO) {
        if (complaintDTO == null) {
            return new ResponseEntity<>("Data is missing.", HttpStatus.BAD_REQUEST);
        }

        User accused = userService.findByID(accusedID);
        if (accused == null) {
            return new ResponseEntity<>("Accused user with id: " + accusedID + " not found.", HttpStatus.NOT_FOUND);
        }

        Complaint complaint = mapper.toEntity(complaintDTO);

        UserService.isBlocked(accused);

        complaint.setAccused(accused);
        userService.addComplaint(accused, complaint);

        LOG.debug("Created complaint {}.", complaintDTO);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{id}", complaint.getId());
        return new ResponseEntity<>(complaint.toString(), headers, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/{complaintID}")
    public ResponseEntity<String> deleteComplaint(@PathVariable Integer complaintID) {
        Complaint complaint = complaintService.findByID(complaintID);

        if (complaint == null) {
            return new ResponseEntity<>("Complaint with id: " + complaintID + " was not found.", HttpStatus.NOT_FOUND);
        }
        userService.removeComplaint(complaint.getAccused(), complaint);

        return new ResponseEntity<>("Complaint with id: " + complaintID + " was successfully deleted.", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/{accusedID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ComplaintDTO> getComplaintsToUser(@PathVariable Integer accusedID) {
        User accused = userService.findByID(accusedID);
        if (accused == null) {
            return null;
        }
        return complaintService.findByAccused(accused)
                .stream()
                .map(mapper::toDto)
                .toList();
    }
}
