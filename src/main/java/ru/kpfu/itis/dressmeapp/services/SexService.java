package ru.kpfu.itis.dressmeapp.services;

import java.util.List;

/**
 * Created by Melnikov Semen
 * 11-601 ITIS KPFU
 */
public interface SexService {
    List<String> getBodyTypeValuesBySexValue(String sex);
}
