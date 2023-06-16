package ru.practicum.shareit.item.comments;

import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    public  Comment toComment(final CommentDto commentDto) {
        return Comment.builder()
                .id(commentDto.getId())
                .text(commentDto.getText())
                .created(commentDto.getCreated())
                .build();
    }

    public CommentDto toCommentDto(final Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .itemId(comment.getItem().getId())
                .created(comment.getCreated())
                .build();
    }

}
