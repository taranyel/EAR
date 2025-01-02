package joinMe.rest.dto;

import joinMe.db.entity.*;
import org.springframework.stereotype.Component;

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
                .id(trip.getId())
                .created(trip.getCreated())
                .description(trip.getDescription())
                .title(trip.getTitle())
                .endDate(trip.getEndDate())
                .imagePath(trip.getImagePath())
                .country(trip.getCountry())
                .status(trip.getStatus())
                .capacity(trip.getCapacity())
                .comments(comments)
                .startDate(trip.getStartDate())
                .author(toDto(trip.getAuthor()))
                .build();
    }

    public UserDTO toDto(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .rating(user.getRating())
                .firstName(user.getFirstName())
                .birthDate(user.getBirthdate())
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
                .id(address.getId())
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
                .id(attendlist.getId())
                .joiner(toDto(attendlist.getJoiner()))
                .trip(toDto(attendlist.getTrip()))
                .build();
    }

    public CommentDTO toDto(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .author(toDto(comment.getAuthor()))
                .text(comment.getText())
                .trip(toDto(comment.getTrip()))
                .build();
    }

    public ComplaintDTO toDto(Complaint complaint) {
        return ComplaintDTO.builder()
                .id(complaint.getId())
                .accused(toDto(complaint.getAccused()))
                .description(complaint.getDescription())
                .build();
    }

    public JoinRequestDTO toDto(JoinRequest joinRequest) {
        return JoinRequestDTO.builder()
                .id(joinRequest.getId())
                .status(joinRequest.getStatus())
                .requester(toDto(joinRequest.getRequester()))
                .trip(toDto(joinRequest.getTrip()))
                .build();
    }

    public MessageDTO toDto(Message message) {
        return MessageDTO.builder()
                .id(message.getId())
                .attendlist(toDto(message.getAttendlist()))
                .author(toDto(message.getAuthor()))
                .text(message.getText())
                .build();
    }

    public Address toEntity(AddressDTO addressDTO) {
        if (Objects.equals(addressDTO.getType(), "flat")) {
            return Flat.builder()
                    .id(addressDTO.getId())
                    .number(addressDTO.getNumber())
                    .country(addressDTO.getCountry())
                    .postIndex(addressDTO.getPostIndex())
                    .city(addressDTO.getCity())
                    .street(addressDTO.getStreet())
                    .build();
        } else {
            return House.builder()
                    .id(addressDTO.getId())
                    .number(addressDTO.getNumber())
                    .country(addressDTO.getCountry())
                    .postIndex(addressDTO.getPostIndex())
                    .city(addressDTO.getCity())
                    .street(addressDTO.getStreet())
                    .build();
        }
    }

    public User toEntity(UserDTO userDTO) {
        return User.builder()
                .address(toEntity(userDTO.getAddress()))
                .status(userDTO.getStatus())
                .rating(userDTO.getRating())
                .birthdate(userDTO.getBirthDate())
                .firstName(userDTO.getFirstName())
                .email(userDTO.getEmail())
                .imagePath(userDTO.getImagePath())
                .lastName(userDTO.getLastName())
                .rating(userDTO.getRating())
                .role(userDTO.getRole())
                .username(userDTO.getUsername())
                .password(userDTO.getPassword())
                .build();
    }

    public Trip toEntity(TripDTO tripDTO) {
        return Trip.builder()
                .startDate(tripDTO.getStartDate())
                .endDate(tripDTO.getEndDate())
                .capacity(tripDTO.getCapacity())
                .description(tripDTO.getDescription())
                .imagePath(tripDTO.getImagePath())
                .title(tripDTO.getTitle())
                .country(tripDTO.getCountry())
                .build();
    }

    public Attendlist toEntity(AttendlistDTO attendlistDTO) {
        List<Message> messages = attendlistDTO.getMessages()
                .stream()
                .map(this::toEntity)
                .toList();

        return Attendlist.builder()
                .id(attendlistDTO.getId())
                .trip(toEntity(attendlistDTO.getTrip()))
                .messages(messages)
                .joiner(toEntity(attendlistDTO.getJoiner()))
                .build();
    }

    public Message toEntity(MessageDTO messageDTO) {
        return Message.builder()
                .id(messageDTO.getId())
                .text(messageDTO.getText())
                .author(toEntity(messageDTO.getAuthor()))
                .attendlist(toEntity(messageDTO.getAttendlist()))
                .build();
    }

    public Comment toEntity(CommentDTO commentDTO) {
        return Comment.builder()
                .id(commentDTO.getId())
                .text(commentDTO.getText())
                .trip(toEntity(commentDTO.getTrip()))
                .author(toEntity(commentDTO.getAuthor()))
                .build();
    }

    public Complaint toEntity(ComplaintDTO complaintDTO) {
        return Complaint.builder()
                .id(complaintDTO.getId())
                .accused(toEntity(complaintDTO.getAccused()))
                .description(complaintDTO.getDescription())
                .build();
    }

    public Wishlist toEntity(WishlistDTO wishlistDTO) {
        return Wishlist.builder()
                .id(wishlistDTO.getId())
                .owner(toEntity(wishlistDTO.getOwner()))
                .trip(toEntity(wishlistDTO.getTrip()))
                .build();
    }
}
