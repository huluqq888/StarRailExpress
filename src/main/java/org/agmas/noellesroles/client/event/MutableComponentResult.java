package org.agmas.noellesroles.client.event;

import net.minecraft.network.chat.MutableComponent;
import org.agmas.noellesroles.client.hud.CommonClientHudRenderer;

import java.util.ArrayList;

public class MutableComponentResult {
    public CommonClientHudRenderer.MessageDetail singleContent = null;
    public ArrayList<CommonClientHudRenderer.MessageDetail> mutipleContent = new ArrayList<>();

    public MutableComponentResult() {

    }

    public MutableComponentResult(CommonClientHudRenderer.MessageDetail content) {
        this.singleContent = content;
    }
    public MutableComponentResult(MutableComponent content) {
        this.singleContent = new CommonClientHudRenderer.MessageDetail( content,false);
    }
}
