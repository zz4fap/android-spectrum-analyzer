#include "Complex.hh"
#include <android/log.h>

#define ANDROID_DEBUG_TAG "ComplexUnitTest"
    
// sample client for testing
public static void main(String[] args) {
	Complex a = Complex(5.0, 6.0);
	Complex b = Complex(-3.0, 4.0);
	
	__android_log_print(ANDROID_LOG_INFO, ANDROID_DEBUG_TAG, "a = %s",a.toString());
	__android_log_print(ANDROID_LOG_INFO, ANDROID_DEBUG_TAG, "b = %s",b.toString());
	
	__android_log_print(ANDROID_LOG_INFO, ANDROID_DEBUG_TAG, "b = %s",b.toString());
	__android_log_print(ANDROID_LOG_INFO, ANDROID_DEBUG_TAG, "b = %s",b.toString());
	__android_log_print(ANDROID_LOG_INFO, ANDROID_DEBUG_TAG, "b = %s",b.toString());
	__android_log_print(ANDROID_LOG_INFO, ANDROID_DEBUG_TAG, "b = %s",b.toString());
	__android_log_print(ANDROID_LOG_INFO, ANDROID_DEBUG_TAG, "b = %s",b.toString());
	__android_log_print(ANDROID_LOG_INFO, ANDROID_DEBUG_TAG, "b = %s",b.toString());
	__android_log_print(ANDROID_LOG_INFO, ANDROID_DEBUG_TAG, "b = %s",b.toString());
	__android_log_print(ANDROID_LOG_INFO, ANDROID_DEBUG_TAG, "b = %s",b.toString());
	__android_log_print(ANDROID_LOG_INFO, ANDROID_DEBUG_TAG, "b = %s",b.toString());
				

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
