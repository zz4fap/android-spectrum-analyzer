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
    
bool Complex::operator==(Complex b) {
	if(this.re==b.re && this.im==b.im) {
		return true;
	} else {
		return false;
	}
}
