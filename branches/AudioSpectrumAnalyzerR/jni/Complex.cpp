#include "Complex.hh"

    // create a new object with the given real and imaginary parts
   Complex::Complex(double real, double imag) {
        re = real;
        im = imag;
    }

    // return a string representation of the invoking Complex object
    string Complex::toString() {
        if (im == 0) return re + "";
        if (re == 0) return im + "i";
        if (im <  0) return re + " - " + (-im) + "i";
        return re + " + " + im + "i";
    }

    // return abs/modulus/magnitude and angle/phase/argument
    double Complex::abs() { 
    	return sqrt(re*re + im*im);
    }  
    
    // between -pi and pi
    double Complex::phase() { 
    	return atan2(im, re); 
    }

    // return a new Complex object whose value is (this + b)
    Complex Complex::operator+(Complex b) {
        double real = this.re + b.re;
        double imag = this.im + b.im;
        return Complex(real, imag);
    }

    // return a new Complex object whose value is (this - b)
    Complex Complex::operator-(Complex b) {
        double real = this.re - b.re;
        double imag = this.im - b.im;
        return Complex(real, imag);
    }

    // return a new Complex object whose value is (this * b)
    Complex Complex::operator*(Complex b) {
        double real = this.re * b.re - this.im * b.im;
        double imag = this.re * b.im + this.im * b.re;
        return Complex(real, imag);
    }

    // scalar multiplication
    // return a new object whose value is (this * alpha)
    Complex Complex::operator*(double alpha) {
        return Complex(alpha * re, alpha * im);
    }
    
    Complex operator*(double alpha, Complex complex) {
    	return Complex(complex.re*alpha, complex.im*alpha);
    }

    // return a new Complex object whose value is the conjugate of this
    Complex Complex::conjugate() {  
    	return Complex(re, -im); 
    }

    // return a new Complex object whose value is the reciprocal of this
    Complex Complex::reciprocal() {
        double scale = re*re + im*im;
        return Complex(re / scale, -im / scale);
    }

    // return the real or imaginary part
    double Complex::re() { 
    	return re; 
    }
    
    double Complex::im() { 
    	return im; 
    }

    // return a / b
    Complex Complex::operator/(Complex b) {
        return this*(b.reciprocal());
    }

    // return a new Complex object whose value is the complex exponential of this
    Complex Complex::exp() {
        return Complex(Math.exp(re) * Math.cos(im), Math.exp(re) * Math.sin(im));
    }

    // return a new Complex object whose value is the complex sine of this
    Complex Complex::sin() {
        return Complex(Math.sin(re) * Math.cosh(im), Math.cos(re) * Math.sinh(im));
    }

    // return a new Complex object whose value is the complex cosine of this
    Complex Complex::cos() {
        return Complex(Math.cos(re) * Math.cosh(im), -Math.sin(re) * Math.sinh(im));
    }

    // return a new Complex object whose value is the complex tangent of this
    Complex Complex::tan() {
        return sin().divides(cos());
    }
    
    // a static version of plus
    public static Complex Complex::plus(Complex a, Complex b) {
        double real = a.re + b.re;
        double imag = a.im + b.im;
        Complex sum = Complex(real, imag);
        return sum;
    }

    // sample client for testing
    public static void main(String[] args) {
        Complex a = new Complex(5.0, 6.0);
        Complex b = new Complex(-3.0, 4.0);

        System.out.println("a            = " + a);
        System.out.println("b            = " + b);
        System.out.println("Re(a)        = " + a.re());
        System.out.println("Im(a)        = " + a.im());
        System.out.println("b + a        = " + b.plus(a));
        System.out.println("a - b        = " + a.minus(b));
        System.out.println("a * b        = " + a.times(b));
        System.out.println("b * a        = " + b.times(a));
        System.out.println("a / b        = " + a.divides(b));
        System.out.println("(a / b) * b  = " + a.divides(b).times(b));
        System.out.println("conj(a)      = " + a.conjugate());
        System.out.println("|a|          = " + a.abs());
        System.out.println("tan(a)       = " + a.tan());
    }
}
