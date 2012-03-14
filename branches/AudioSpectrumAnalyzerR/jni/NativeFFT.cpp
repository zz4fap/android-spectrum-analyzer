#include "Fft.hh"
#include "FFTNativeHelper.hh"

using namespace android;

static FFTNativeHelper *mFFTNativeHelper = NULL;

jboolean Java_com_spectrumanalyzer_fft_NativeFFTHelper_initFft(JNIEnv* env, jclass clazz, jdouble sampleRate, jint fftSize) {

	mFFTNativeHelper = new FFTNativeHelper(sampleRate, fftSize);
	if(mFFTNativeHelper)
		return true;
	else
		return false;
}

jdoubleArray Java_com_spectrumanalyzer_fft_NativeFFTHelper_calculateFFT(JNIEnv* env, jclass clazz, jbyteArray jSignal, jint numberOfReadBytes) {

	jdouble *mAbsSignal = NULL;
	jdoubleArray output = NULL;

	if(mFFTNativeHelper) {
		jbyte* buffer = env->GetByteArrayElements(env, jSignal, NULL);

		mAbsSignal = mFFTNativeHelper->calculateFFT(buffer, numberOfReadBytes);
		env->ReleaseByteArrayElements(env, buffer, 0);

		//Copy mAbsSignal to a double array
		int size = mFFTNativeHelper->getFFTSize()/2;
		output = env->NewDoubleArray(size);
		env->SetDoubleArrayRegion(output, 0, size, mAbsSignal);
	}

	return output;
}
