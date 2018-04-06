package ru.kpfu.itis.dressmeapp.services;

import ru.kpfu.itis.dressmeapp.model.ClassificationWaiting;
import ru.kpfu.itis.dressmeapp.model.User; /**
 * Created by Melnikov Semen
 * 11-601 ITIS KPFU
 */
public interface ClassificationWaitingService {
    ClassificationWaiting createWaitingNote(User user);

    void setStatusDone(ClassificationWaiting cw);

    boolean isUserInWaiting(User user);
}
