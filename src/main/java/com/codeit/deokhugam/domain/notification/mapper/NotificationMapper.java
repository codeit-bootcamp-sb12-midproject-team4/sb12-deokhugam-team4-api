package com.codeit.deokhugam.domain.notification.mapper;

import com.codeit.deokhugam.domain.notification.dto.NotificationResponse;
import com.codeit.deokhugam.domain.notification.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

  @Mapping(source = "user.id", target = "userId")
  @Mapping(source = "review.id", target = "reviewId")
  @Mapping(source = "review.content", target = "reviewContent")
  NotificationResponse toResponse(Notification notification);
}
