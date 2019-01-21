package com.github.alastairbooth.imageeffects.random;

import com.github.alastairbooth.imageeffects.ImageGenerator;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Perlin implements ImageGenerator {

    private long seed;
    private double octaves;
    private double amplitude;
    private double cloudDensity;
    private double frequency;
    private double persistence;
    private double cloudCoverage;
    private int r1, r2, r3;

    public Perlin(long seed){
        this(seed, 8, 1, 1, .015, .65, 0);
    }

    public Perlin(long seed, double octaves, double amplitude, double cloudDensity, double frequency, double persistence, double cloudCoverage){
        this.seed = seed;
        this.octaves = octaves;
        this.amplitude = amplitude;
        this.cloudDensity = cloudDensity;
        this.frequency = frequency;
        this.persistence = persistence;
        this.cloudCoverage = cloudCoverage;

        Random random = new Random(seed);
        r1 = random.nextInt(1000);
        r2 = random.nextInt(100000);
        r3 = random.nextInt(1000000000);
    }

    public long getSeed() {
        return seed;
    }

    public double getHeight(int x, int y){
        double total = 0.0;

        for(int i = 0; i < octaves; i++) {
            total = total + Smooth(x * frequency, y * frequency, r1, r2, r3) * amplitude;
            frequency = frequency * 2;
            amplitude = amplitude * persistence;
        }

        total = (total + cloudCoverage) * cloudDensity;

        if(total < 0) {
            total = 0.0;
        }
        if(total > 1) {
            total = 1.0;
        }

        return total;
    }

    public double[][] getHeightmap(int x, int y, int width, int height){
        double[][] heightMap = new double[width][height];
        for(int i = x; i < (x + width); i++){
            for(int j = y; j < (y + height); j++){
                heightMap[i][j] = getHeight(i, j);
            }
        }
        return heightMap;
    }

    public BufferedImage generate(int width, int height) {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.createGraphics();
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                double heightValue = getHeight(i, j);
                //TODO
            }
        }
        return image;
    }

    private double Smooth(double x, double y, int r1, int r2, int r3){
        double n1 = Noise((int)x, (int)y, r1, r2, r3);
        double n2 = Noise((int)x + 1, (int)y, r1, r2, r3);
        double n3 = Noise((int)x, (int)y + 1, r1, r2, r3);
        double n4 = Noise((int)x + 1, (int)y + 1, r1, r2, r3);

        double i1 = interpolate(n1, n2, x - (int)x);
        double i2 = interpolate(n3, n4, x - (int)x);

        return interpolate(i1, i2, y - (int)y);
    }

    private double Noise(int x, int y, int r1, int r2, int r3){
        int n = x + y * 57;
        n = (n<<13) ^ n;
        return ( 1.0 - ( (n * (n * n * r1 + r2) + r3) & 0x7fffffff) / 1073741824.0);
    }

    private double interpolate(double x, double y, double a){
        double val = (1 - Math.cos(a * Math.PI)) * .5;
        return  x * (1 - val) + y * val;
    }
}
