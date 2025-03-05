package com.example.backend.config;

import com.example.backend.dto.chapter.ChapterDetailDto;
import com.example.backend.dto.comment.CommentRequestDto;
import com.example.backend.entity.Chapter;
import com.example.backend.entity.ChapterGroup;
import com.example.backend.entity.Comment;
import com.example.backend.entity.Novel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ModelMapperConfigTest {

    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        ModelMapperConfig config = new ModelMapperConfig();
        modelMapper = config.modelMapper();
    }

    @Nested
    @DisplayName("CommentRequestDto to Comment Mapping Tests")
    class CommentRequestDtoMappingTests {

        @Test
        void whenMappingCommentRequestDto_ShouldMapBasicFields() {
            CommentRequestDto requestDto = new CommentRequestDto(1L, 2L, "Content");

            Novel novel = new Novel();
            novel.setId(1L);
            Chapter chapter = new Chapter();
            chapter.setId(2L);
            Comment comment = new Comment(null, novel, chapter, null, "Content", null);

            Comment result = modelMapper.map(requestDto, Comment.class);

            assertAll(
                    () -> assertEquals(comment.getId(), result.getId()),
                    () -> assertEquals(comment.getNovel().getId(), result.getNovel().getId()),
                    () -> assertEquals(comment.getChapter().getId(), result.getChapter().getId()),
                    () -> assertEquals(comment.getContent(), result.getContent()),
                    () -> assertEquals(comment.getUser(), result.getUser()),
                    () -> assertEquals(comment.getCreatedAt(), result.getCreatedAt())
            );
        }

        @Test
        void whenNovelIdIsNull_ShouldNotSetNovel() {
            CommentRequestDto requestDto = new CommentRequestDto(null, 2L, "Content");

            Novel novel = new Novel();
            Chapter chapter = new Chapter();
            chapter.setId(2L);
            Comment comment = new Comment(null, novel, chapter, null, "Content", null);

            Comment result = modelMapper.map(requestDto, Comment.class);

            assertAll(
                    () -> assertEquals(comment.getId(), result.getId()),
                    () -> assertNull(result.getNovel()),
                    () -> assertEquals(comment.getChapter().getId(), result.getChapter().getId()),
                    () -> assertEquals(comment.getContent(), result.getContent()),
                    () -> assertEquals(comment.getUser(), result.getUser()),
                    () -> assertEquals(comment.getCreatedAt(), result.getCreatedAt())
            );
        }

        @Test
        void whenChapterIdIsNull_ShouldNotSetChapter() {
            CommentRequestDto requestDto = new CommentRequestDto(1L, null, "Content");

            Novel novel = new Novel();
            novel.setId(1L);
            Chapter chapter = new Chapter();
            Comment comment = new Comment(null, novel, chapter, null, "Content", null);

            Comment result = modelMapper.map(requestDto, Comment.class);

            assertAll(
                    () -> assertEquals(comment.getId(), result.getId()),
                    () -> assertEquals(comment.getNovel().getId(), result.getNovel().getId()),
                    () -> assertNull(result.getChapter()),
                    () -> assertEquals(comment.getContent(), result.getContent()),
                    () -> assertEquals(comment.getUser(), result.getUser()),
                    () -> assertEquals(comment.getCreatedAt(), result.getCreatedAt())
            );
        }
    }

    @Nested
    @DisplayName("Chapter to ChapterDetailDto Mapping Tests")
    class ChapterMappingTests {

        @Test
        void whenMappingChapter_ShouldMapBasicFields() {
            ChapterGroup chapterGroup =
                    new ChapterGroup(1L, null, "Test Group", 0.5, null);
            Set<Comment> comments = Set.of(new Comment(), new Comment());
            Chapter chapter = new Chapter(2L, chapterGroup, "Test Chapter", "Content",
                            100, 0.5, null, comments);
            ChapterDetailDto chapterDetailDto = new ChapterDetailDto(2L, "Test Group",
                    "Test Chapter", "Content", 100, 0.5, null, 2);

            ChapterDetailDto result = modelMapper.map(chapter, ChapterDetailDto.class);

            assertAll(
                    () -> assertEquals(chapterDetailDto.getId(), result.getId()),
                    () -> assertEquals(chapterDetailDto.getChapterGroupName(), result.getChapterGroupName()),
                    () -> assertEquals(chapterDetailDto.getName(), result.getName()),
                    () -> assertEquals(chapterDetailDto.getContent(), result.getContent()),
                    () -> assertEquals(chapterDetailDto.getWordsCount(), result.getWordsCount()),
                    () -> assertEquals(chapterDetailDto.getChapterOrder(), result.getChapterOrder()),
                    () -> assertEquals(chapterDetailDto.getCreationDate(), result.getCreationDate()),
                    () -> assertEquals(chapterDetailDto.getCommentCount(), result.getCommentCount())
            );
        }

        @Test
        void whenChapterHasNoComments_ShouldSetCommentCountToZero() {
            Chapter chapter = new Chapter(null, new ChapterGroup(), null, null, 0, 0, null, Set.of());
            ChapterDetailDto chapterDetailDto = new ChapterDetailDto(null, null, null, null, 0, 0, null, 0);

            ChapterDetailDto result = modelMapper.map(chapter, ChapterDetailDto.class);

            assertEquals(chapterDetailDto.getCommentCount(), result.getCommentCount());
        }
    }
}
