package com.plovdev.pronviewer.pornimpl.sexstuds;

import com.plovdev.pronviewer.httpquering.PornChecker;
import com.plovdev.pronviewer.httpquering.PornParser;
import com.plovdev.pronviewer.httpquering.PornVideoAdapter;
import com.plovdev.pronviewer.httpquering.Resourcer;

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