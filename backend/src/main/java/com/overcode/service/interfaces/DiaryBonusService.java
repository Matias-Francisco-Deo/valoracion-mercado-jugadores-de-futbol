package com.overcode.service.interfaces;

import com.overcode.model.bonus.DiaryBonus;

public interface DiaryBonusService {
    DiaryBonus recuperarSeleccionado();
    DiaryBonus guardar(DiaryBonus diaryBonus);
}
