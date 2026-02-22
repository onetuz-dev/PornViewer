package com.plovdev.pornviewer.pornimpl.sexstuds;

import com.plovdev.pornviewer.httpquering.PornChecker;
import com.plovdev.pornviewer.httpquering.PornParser;
import com.plovdev.pornviewer.httpquering.PornVideoAdapter;
import com.plovdev.pornviewer.httpquering.Resourcer;

public class SexStudsVideoAdapter implements PornVideoAdapter {
    @Override
    public PornParser getParser() {
        return new SexStudsParser();
    }

    @Override
    public PornChecker getChecker() {
        return new SexStudsChecker();
    }

    @Override
    public Resourcer getResourcer() {
        return new SSRes();
    }
}