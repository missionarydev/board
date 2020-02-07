package me.missionary.board.settings;

import lombok.Builder;
import lombok.Getter;
import me.missionary.board.provider.BoardProvider;

/**
 * @author Missionary (missionarymc@gmail.com)
 * @since 5/31/2018
 */
@Getter
@Builder
public class BoardSettings {

    private BoardProvider boardProvider;

    private ScoreDirection scoreDirection;

}
