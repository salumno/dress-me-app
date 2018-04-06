package ru.kpfu.itis.dressmeapp.services.impl;

import org.springframework.stereotype.Service;
import ru.kpfu.itis.dressmeapp.model.ClassificationWaiting;
import ru.kpfu.itis.dressmeapp.model.ClassificationWaitingStatus;
import ru.kpfu.itis.dressmeapp.model.User;
import ru.kpfu.itis.dressmeapp.repositories.ClassificationWaitingRepository;
import ru.kpfu.itis.dressmeapp.services.ClassificationWaitingService;

/**
 * Created by Melnikov Semen
 * 11-601 ITIS KPFU
 */

@Service
public class ClassificationWaitingServiceImpl implements ClassificationWaitingService {
    private ClassificationWaitingRepository classificationWaitingRepository;

    public ClassificationWaitingServiceImpl(ClassificationWaitingRepository classificationWaitingRepository) {
        this.classificationWaitingRepository = classificationWaitingRepository;
    }

    @Override
    public ClassificationWaiting createWaitingNote(User user) {
        ClassificationWaiting cw = ClassificationWaiting.builder()
                .user(user)
                .status(ClassificationWaitingStatus.IN_PROCESS)
                .startTime(System.currentTimeMillis())
                .build();
        classificationWaitingRepository.save(cw);
        return cw;
    }

    @Override
    public void setStatusDone(ClassificationWaiting cw) {
        cw.setEndTime(System.currentTimeMillis());
        cw.setStatus(ClassificationWaitingStatus.DONE);
        classificationWaitingRepository.save(cw);
    }

    @Override
    public boolean isUserInWaiting(User user) {
        return classificationWaitingRepository
                .findAllByUserIdAndStatus(user.getId(), ClassificationWaitingStatus.IN_PROCESS)
                .size() > 0;
    }

}
