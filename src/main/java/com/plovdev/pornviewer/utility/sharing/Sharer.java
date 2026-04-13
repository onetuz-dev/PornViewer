package com.plovdev.pornviewer.utility.sharing;

import com.plovdev.pornviewer.gui.toast.Toast;
import com.plovdev.pornviewer.gui.utils.ClipboardUtils;
import com.plovdev.pornviewer.models.ModelCard;
import com.plovdev.pornviewer.models.PornCard;
import com.plovdev.pornviewer.models.VideoCard;
import javafx.stage.Stage;

public class Sharer {
    public static void share(Stage owner, PornCard card, ShareParameter ... parameters) {
        String shareMethod = switch (card) {
            case ModelCard model -> "model";
            case VideoCard video -> "video";
            default -> throw new IllegalArgumentException("Invalid share method: " + card);
        };

        StringBuilder shareParams = new StringBuilder();

        for (ShareParameter parameter : parameters) {
            shareParams.append("&").append(parameter);
        }

        String shareUrl = "pv://share/" + shareMethod + "?" + shareParams;
        ClipboardUtils.copy(shareUrl);
        new Toast(owner, "Ссылка скопирована").show();
    }
}
