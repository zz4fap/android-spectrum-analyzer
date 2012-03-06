#ifndef COMPLEX_HH
#define COMPLEX_HH

/*************************************************************************
 *
 *  Data type for complex numbers.
 *  
 *  a            = 5.0 + 6.0i
 *  b            = -3.0 + 4.0i
 *  Re(a)        = 5.0
 *  Im(a)        = 6.0
 *  b + a        = 2.0 + 10.0i
 *  a - b        = 8.0 + 2.0i
 *  a * b        = -39.0 + 2.0i
 *  b * a        = -39.0 + 2.0i
 *  a / b        = 0.36 - 1.52i
 *  (a / b) * b  = 5.0 + 6.0i
 *  conj(a)      = 5.0 - 6.0i
 *  |a|          = 7.810249675906654
 *
 *************************************************************************/

class Complex {

	private:
		double re;   // the real part
		double im;   // the imaginary part
		
	public:
		Complex(double,double);
		string toString();
		friend Complex operator*(double,Complex);
		double abs();
		double phase();
		Complex operator+(Complex);
		Complex operator-(Complex);
		Complex operator*(Complex);
		Complex operator*(double);
		Complex conjugate();
		Complex reciprocal();
		double re();
		double im();
		Complex operator/(Complex);
		bool operator==(Complex);
		Complex operator=(Complex&);
};

#endif //COMPLEX_HH
