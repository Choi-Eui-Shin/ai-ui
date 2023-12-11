package com.choi.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Rectangle {
	private int x, y, width, height;
	
	public Rectangle() {
	}

    public Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int area() {
        return width * height;
    }

    public Rectangle intersection(Rectangle other) {
        int x = Math.max(this.x, other.x);
        int y = Math.max(this.y, other.y);
        int width = Math.min(this.x + this.width, other.x + other.width) - x;
        int height = Math.min(this.y + this.height, other.y + other.height) - y;

        if (width <= 0 || height <= 0) {
            return null; // No intersection
        }

        return new Rectangle(x, y, width, height);
    }
    
    public static void main(String [] args) {
    	Rectangle r1 = new Rectangle(0, 0, 100, 100);
    	Rectangle r2 = new Rectangle(10, 10, 10, 10);
    	
    	System.out.println(r2.area());
    	
    	Rectangle intersection = r2.intersection(r1);
    	if(intersection != null) {
    		System.out.println(intersection.area());
    		System.out.println(intersection.area()/r2.area());
    	}
    }
}
