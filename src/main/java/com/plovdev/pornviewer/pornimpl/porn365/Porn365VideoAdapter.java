package com.plovdev.pornviewer.pornimpl.porn365;

import com.plovdev.pornviewer.httpquering.PornChecker;
import com.plovdev.pornviewer.httpquering.PornParser;
import com.plovdev.pornviewer.httpquering.PornVideoAdapter;
import com.plovdev.pornviewer.httpquering.Resourcer;

public class Porn365VideoAdapter implements PornVideoAdapter {
    @Override
    public PornParser getParser() {
        return new DefPornParser();
    }

    @Override
    public PornChecker getChecker() {
        return new Porn365Checker();
    }

    @Override
    public Resourcer getResourcer() {
        return new DefRes();
    }
}