package com.ewoudje.townskings.town.plot;

import com.ewoudje.townskings.api.town.Plot;

import java.util.ArrayList;
import java.util.List;

public class ChunkMap {

    private List<Cube> claims = null;
    private Plot plot = null;

    public ChunkMap() {
        this.claims = new ArrayList<>();
    }

    public ChunkMap(Plot plot) {
        this.plot = plot;
    }


    private class Cube {
        int lX, lY, lZ;
        int uX, uY, uZ;
        Plot plot;
    }
}
