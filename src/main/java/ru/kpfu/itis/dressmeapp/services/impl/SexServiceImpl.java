package ru.kpfu.itis.dressmeapp.services.impl;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;
import ru.kpfu.itis.dressmeapp.model.FaceFemaleBodyType;
import ru.kpfu.itis.dressmeapp.model.FaceMaleBodyType;
import ru.kpfu.itis.dressmeapp.services.SexService;

import java.util.List;

/**
 * Created by Melnikov Semen
 * 11-601 ITIS KPFU
 */

@Service
public class SexServiceImpl implements SexService {

    @Override
    public List<String> getBodyTypeValuesBySexValue(String sex) {
        String capitalizedSex = sex.toUpperCase();
        List<String> bodyTypes = Lists.newArrayList();
        if (capitalizedSex.equals("FEMALE")) {
            for (FaceFemaleBodyType type: FaceFemaleBodyType.values()) {
                bodyTypes.add(type.toString());
            }
        } else if (capitalizedSex.equals("MALE")) {
            for (FaceMaleBodyType type: FaceMaleBodyType.values()) {
                bodyTypes.add(type.toString());
            }
        }
        return bodyTypes;
    }
}
