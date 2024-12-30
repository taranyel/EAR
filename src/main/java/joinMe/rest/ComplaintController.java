package joinMe.rest;

import joinMe.db.entity.Complaint;
import joinMe.db.entity.Role;
import joinMe.db.entity.User;
import joinMe.db.exception.NotFoundException;
import joinMe.rest.dto.ComplaintDTO;
import joinMe.rest.dto.Mapper;
import joinMe.rest.util.RestUtils;
import joinMe.security.model.UserDetails;
import joinMe.service.ComplaintService;
import joinMe.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createComplaint(Authentication auth, @RequestBody ComplaintDTO complaintDTO) {
        assert auth.getPrincipal() instanceof UserDetails;
        final User user = ((UserDetails) auth.getPrincipal()).getUser();

        Complaint complaint = mapper.toEntity(complaintDTO);

        complaintService.persist(complaint);
        userService.addComplaint(user, complaint);

        LOG.debug("Created complaint {}.", complaintDTO);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{id}", complaint.getId());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/{id}")
    public void deleteComplaint(Authentication auth, @PathVariable int id) {
        assert auth.getPrincipal() instanceof UserDetails;
        final User user = ((UserDetails) auth.getPrincipal()).getUser();

        Complaint complaint = complaintService.findByID(id);
        if (complaint == null) {
            throw NotFoundException.create("Compaint", id);
        }

        if (user.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Cannot access complaint without ADMIN role.");
        }
        userService.removeComplaint(user, complaint);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ComplaintDTO> getComplaintsToCurrentUser(Authentication auth) {
        assert auth.getPrincipal() instanceof UserDetails;
        final User user = ((UserDetails) auth.getPrincipal()).getUser();
        return user.getComplaints()
                .stream()
                .map(mapper::toDto)
                .toList();
    }
}
