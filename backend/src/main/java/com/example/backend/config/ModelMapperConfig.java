package com.example.backend.config;

import com.example.backend.dto.chapter.ChapterDetailDto;
import com.example.backend.dto.comment.CommentRequestDto;
import com.example.backend.dto.user.UserDetailDto;
import com.example.backend.entity.Chapter;
import com.example.backend.entity.Comment;
import com.example.backend.entity.Novel;
import com.example.backend.entity.User;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);

        mapper.createTypeMap(CommentRequestDto.class, Comment.class)
                .addMappings(mapping -> mapping.skip(Comment::setUser))
                .setPostConverter(context -> {
                    CommentRequestDto source = context.getSource();
                    Comment destination = context.getDestination();

                    if (source.getNovelId() != null) {
                        Novel novel = new Novel();
                        novel.setId(source.getNovelId());
                        destination.setNovel(novel);
                    }

                    if (source.getChapterId() != null) {
                        Chapter chapter = new Chapter();
                        chapter.setId(source.getChapterId());
                        destination.setChapter(chapter);
                    }
                    return destination;
                });

        mapper.createTypeMap(Chapter.class, ChapterDetailDto.class)
                .setPostConverter(context -> {
                    Chapter source = context.getSource();
                    ChapterDetailDto destination = context.getDestination();

                    destination.setCommentCount(source.getComments().size());
                    destination.setChapterGroupName(source.getChapterGroup().getName());
                    return destination;
                });

        mapper.createTypeMap(User.class, UserDetailDto.class)
                .setPostConverter(context -> {
                    User source = context.getSource();
                    UserDetailDto destination = context.getDestination();

                    destination.setCommentsCount(source.getComments().size());

                    destination.setChaptersCount(
                            source.getOwnNovels().stream()
                                    .mapToInt(novel -> novel.getChapterGroups().stream()
                                            .mapToInt(chapterGroup -> chapterGroup.getChapters().size())
                                            .sum()
                                    ).sum()
                    );

                    return destination;
                });

        return mapper;
    }
}


