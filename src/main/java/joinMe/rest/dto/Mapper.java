package joinMe.rest.dto;

import joinMe.db.entity.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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

    public RatingDTO toDto(Rating rating) {
        return RatingDTO.builder()
                .rating(rating.getRating())
                .comment(rating.getComment())
                .build();
    }

    public UserDTO forOthers(User user) {
        List<RatingDTO> ratings = user
                .getRatings()
                .stream()
                .map(this::toDto)
                .toList();

        return UserDTO.builder()
                .id(user.getId())
                .averageRating(user.getAverageRating())
                .ratings(ratings)
                .firstName(user.getFirstName())
                .birthDate(user.getBirthdate())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .imagePath(user.getImagePath())
                .build();
    }

    public UserDTO toDto(User user) {
        List<RatingDTO> ratings = user
                .getRatings()
                .stream()
                .map(this::toDto)
                .toList();

        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .averageRating(user.getAverageRating())
                .ratings(ratings)
                .firstName(user.getFirstName())
                .birthDate(user.getBirthdate())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .imagePath(user.getImagePath())
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
                .authorUsername(comment.getAuthor().getUsername())
                .text(comment.getText())
                .time(comment.getTime())
                .build();
    }

    public ComplaintDTO toDto(Complaint complaint) {
        return ComplaintDTO.builder()
                .id(complaint.getId())
                .accused(toDto(complaint.getAccused()))
                .description(complaint.getDescription())
                .time(complaint.getTime())
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
                .authorUsername(message.getAuthor().getUsername())
                .text(message.getText())
                .time(message.getTime())
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
                .id(userDTO.getId())
                .birthdate(userDTO.getBirthDate())
                .firstName(userDTO.getFirstName())
                .email(userDTO.getEmail())
                .imagePath(userDTO.getImagePath())
                .lastName(userDTO.getLastName())
                .username(userDTO.getUsername())
                .password(userDTO.getPassword())
                .build();
    }

    public Trip toEntity(TripDTO tripDTO) {
        return Trip.builder()
                .id(tripDTO.getId())
                .startDate(tripDTO.getStartDate())
                .endDate(tripDTO.getEndDate())
                .capacity(tripDTO.getCapacity())
                .description(tripDTO.getDescription())
                .imagePath(tripDTO.getImagePath())
                .title(tripDTO.getTitle())
                .country(tripDTO.getCountry())
                .build();
    }

    public Message toEntity(MessageDTO messageDTO) {
        LocalDateTime time = LocalDateTime.now();

        if (messageDTO.getTime() != null) {
            time = messageDTO.getTime();
        }

        return Message.builder()
                .id(messageDTO.getId())
                .text(messageDTO.getText())
                .time(time)
                .build();
    }

    public Rating toEntity(RatingDTO ratingDTO) {
        return Rating.builder()
                .comment(ratingDTO.getComment())
                .rating(ratingDTO.getRating())
                .build();
    }

    public Comment toEntity(CommentDTO commentDTO) {
        LocalDateTime time = LocalDateTime.now();

        if (commentDTO.getTime() != null) {
            time = commentDTO.getTime();
        }

        return Comment.builder()
                .id(commentDTO.getId())
                .text(commentDTO.getText())
                .time(time)
                .build();
    }

    public Complaint toEntity(ComplaintDTO complaintDTO) {
        LocalDateTime time = LocalDateTime.now();

        if (complaintDTO.getTime() != null) {
            time = complaintDTO.getTime();
        }

        return Complaint.builder()
                .id(complaintDTO.getId())
                .description(complaintDTO.getDescription())
                .time(time)
                .build();
    }
}
