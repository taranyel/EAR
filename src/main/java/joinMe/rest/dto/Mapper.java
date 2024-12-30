package joinMe.rest.dto;

import joinMe.db.entity.*;
import org.springframework.stereotype.Component;

import java.util.List;

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
                .birthDate(user.getBirthdate().toString())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .imagePath(user.getImagePath())
                .build();
    }

    public AttendlistDTO toDto(Attendlist attendlist) {
        return AttendlistDTO.builder()
                .trip(toDto(attendlist.getTrip()))
                .build();
    }

    public CommentDTO toDto(Comment comment) {
        return CommentDTO.builder()
                .author(toDto(comment.getAuthor()))
                .text(comment.getText())
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
                .author(toDto(message.getAuthor()))
                .text(message.getText())
                .build();
    }

    public WishlistDTO toDto(Wishlist wishlist) {
        return WishlistDTO.builder()
                .owner(toDto(wishlist.getOwner()))
                .trip(toDto(wishlist.getTrip()))
                .build();
    }
}
