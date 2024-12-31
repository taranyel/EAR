package joinMe.rest.dto;

import joinMe.db.entity.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Component
public class Mapper {

    public TripDTO toDto(Trip trip) {
        List<CommentDTO> comments = trip
                .getComments()
                .stream()
                .map(this::toDto)
                .toList();

        return TripDTO.builder()
                .created(trip.getCreated().toString())
                .description(trip.getDescription())
                .title(trip.getTitle())
                .endDate(trip.getEndDate().toString())
                .imagePath(trip.getImagePath())
                .country(trip.getCountry())
                .status(trip.getStatus().getStatus())
                .capacity(trip.getCapacity().toString())
                .comments(comments)
                .startDate(trip.getStartDate().toString())
                .author(toDto(trip.getAuthor()))
                .build();
    }

    public UserDTO toDto(User user) {
        return UserDTO.builder()
                .email(user.getEmail())
                .role(user.getRole().getName())
                .status(user.getStatus().getStatus())
                .rating(user.getRating().toString())
                .firstName(user.getFirstName())
                .birthdate(user.getBirthdate().toString())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .imagePath(user.getImagePath())
                .address(toDto(user.getAddress()))
                .build();
    }

    public AddressDTO toDto(Address address) {
        String type;
        if (address instanceof Flat) {
            type = "flat";
        } else {
            type = "house";
        }

        return AddressDTO.builder()
                .type(type)
                .city(address.getCity())
                .number(address.getNumber())
                .country(address.getCountry())
                .street(address.getStreet())
                .postIndex(address.getPostIndex())
                .build();
    }

    public AttendlistDTO toDto(Attendlist attendlist) {
        return AttendlistDTO.builder()
                .joiner(toDto(attendlist.getJoiner()))
                .trip(toDto(attendlist.getTrip()))
                .build();
    }

    public CommentDTO toDto(Comment comment) {
        return CommentDTO.builder()
                .author(toDto(comment.getAuthor()))
                .text(comment.getText())
                .trip(toDto(comment.getTrip()))
                .build();
    }

    public ComplaintDTO toDto(Complaint complaint) {
        return ComplaintDTO.builder()
                .accused(toDto(complaint.getAccused()))
                .description(complaint.getDescription())
                .build();
    }

    public JoinRequestDTO toDto(JoinRequest joinRequest) {
        return JoinRequestDTO.builder()
                .status(joinRequest.getStatus().getStatus())
                .requester(toDto(joinRequest.getRequester()))
                .trip(toDto(joinRequest.getTrip()))
                .build();
    }

    public MessageDTO toDto(Message message) {
        return MessageDTO.builder()
                .attendlist(toDto(message.getAttendlist()))
                .author(toDto(message.getAuthor()))
                .text(message.getText())
                .build();
    }

    public Address toEntity(AddressDTO addressDTO) {
        if (Objects.equals(addressDTO.getType(), "flat")) {
            return new Flat(addressDTO.getCity(), addressDTO.getCountry(), addressDTO.getNumber(),
                    addressDTO.getPostIndex(), addressDTO.getStreet());
        } else {
            return new House(addressDTO.getCity(), addressDTO.getCountry(), addressDTO.getNumber(),
                    addressDTO.getPostIndex(), addressDTO.getStreet());
        }
    }

    public User toEntity(UserDTO userDTO) {
        LocalDate date = LocalDate.parse(userDTO.getBirthdate());

        return new User(toEntity(userDTO.getAddress()), date, userDTO.getEmail(), userDTO.getFirstName(),
                userDTO.getImagePath(), userDTO.getLastName(), userDTO.getPassword(), userDTO.getUsername());
    }

    public Trip toEntity(TripDTO tripDTO) {
        LocalDate endDate = LocalDate.parse(tripDTO.getEndDate());
        LocalDate startDate = LocalDate.parse(tripDTO.getStartDate());

        return new Trip(tripDTO.getTitle(), null, Integer.valueOf(tripDTO.getCapacity()),
                tripDTO.getCountry(), tripDTO.getDescription(), endDate, tripDTO.getImagePath(),
                startDate);
    }

    public Attendlist toEntity(AttendlistDTO attendlistDTO) {
        return new Attendlist(toEntity(attendlistDTO.getJoiner()), toEntity(attendlistDTO.getTrip()));
    }

    public Message toEntity(MessageDTO messageDTO) {
        return new Message(toEntity(messageDTO.getAttendlist()), toEntity(messageDTO.getAuthor()), messageDTO.getText());
    }

    public Comment toEntity(CommentDTO commentDTO) {
        return new Comment(toEntity(commentDTO.getAuthor()), commentDTO.getText(), toEntity(commentDTO.getTrip()));
    }

    public Complaint toEntity(ComplaintDTO complaintDTO) {
        return new Complaint(toEntity(complaintDTO.getAccused()), complaintDTO.getDescription());
    }

    public Wishlist toEntity(WishlistDTO wishlistDTO) {
        return new Wishlist(toEntity(wishlistDTO.getOwner()), toEntity(wishlistDTO.getTrip()));
    }
}
