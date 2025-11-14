package com.plovdev.pronviewer.pornimpl.porn365;

import com.plovdev.pronviewer.httpquering.PornChecker;
import com.plovdev.pronviewer.httpquering.PornParser;
import com.plovdev.pronviewer.httpquering.PornVideoAdapter;
import com.plovdev.pronviewer.httpquering.Resourcer;

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