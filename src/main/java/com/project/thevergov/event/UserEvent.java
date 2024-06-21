package com.project.thevergov.event;


import com.project.thevergov.entity.UserEntity;
import com.project.thevergov.enumeration.EventType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class UserEvent {
    private UserEntity user;

    private EventType type;

    private Map<?, ?> data;
}
