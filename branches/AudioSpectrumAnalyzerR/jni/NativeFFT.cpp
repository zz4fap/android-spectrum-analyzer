#include "Fft.hh"
#include "FFTNativeHelper.hh"

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

#define FFT_VERSION 1

static FFTNativeHelper *mFFTNativeHelper = NULL;

JNIEXPORT jboolean JNICALL Java_com_spectrumanalyzer_fft_NativeFFTHelper_initFft(JNIEnv* env, jclass clazz, jdouble sampleRate, jint fftSize) {

	mFFTNativeHelper = new FFTNativeHelper(sampleRate, fftSize);
	if(mFFTNativeHelper)
		return true;
	else
		return false;
}

JNIEXPORT jdoubleArray JNICALL Java_com_spectrumanalyzer_fft_NativeFFTHelper_calculateFFT(JNIEnv* env, jclass clazz, jbyteArray jSignal, jint numberOfReadBytes) {

	jdouble *mAbsSignal = NULL;
	jdoubleArray output = NULL;
	jboolean isCopy1, isCopy2;

	if(mFFTNativeHelper) {
		jbyte* buffer = env->GetByteArrayElements(jSignal, &isCopy1);

		mAbsSignal = mFFTNativeHelper->calculateFFT((char*)buffer, numberOfReadBytes);

		//Copy mAbsSignal to a double array
		int size = mFFTNativeHelper->getFFTSize()/2;
		output = env->NewDoubleArray(size);

	    jdouble* destArrayElems = env->GetDoubleArrayElements(output, &isCopy2);

	    for (int i = 0; i < size; i++) {
	       destArrayElems[i] = mAbsSignal[i];
	    }

	    if (isCopy1 == JNI_TRUE) {
	    	env->ReleaseByteArrayElements(jSignal, buffer, 0);
	    }

	    if (isCopy2 == JNI_TRUE) {
	    	env->ReleaseDoubleArrayElements(output, destArrayElems, 0);
	    }

		//env->SetDoubleArrayRegion(output, 0, size, mAbsSignal);
	}

	return output;
}

JNIEXPORT jint JNICALL Java_com_spectrumanalyzer_fft_NativeFFTHelper_getNativeFftVersion(JNIEnv* env, jclass clazz) {
	return FFT_VERSION;
}

JNIEXPORT jdouble JNICALL Java_com_spectrumanalyzer_fft_NativeFFTHelper_getPeakFrequency(JNIEnv* env, jclass clazz) {
	return mFFTNativeHelper->getPeakFrequency();
}

JNIEXPORT jint JNICALL Java_com_spectrumanalyzer_fft_NativeFFTHelper_getPeakFrequencyPosition(JNIEnv* env, jclass clazz) {
	return mFFTNativeHelper->getPeakFrequencyPosition();
}

JNIEXPORT jdouble JNICALL Java_com_spectrumanalyzer_fft_NativeFFTHelper_getMaxFFTSample(JNIEnv* env, jclass clazz) {
	return mFFTNativeHelper->getMaxFFTSample();
}

JNIEXPORT void JNICALL Java_com_spectrumanalyzer_fft_NativeFFTHelper_setSamplingRate(JNIEnv* env, jclass clazz, jdouble sampleRate) {
	return mFFTNativeHelper->setSamplingRate(sampleRate);
}

JNIEXPORT void JNICALL Java_com_spectrumanalyzer_fft_NativeFFTHelper_setNumberOfFFTPoints(JNIEnv* env, jclass clazz, jint numberOfFFTPoints) {
	return mFFTNativeHelper->setNumberOfFFTPoints(numberOfFFTPoints);
}

#ifdef __cplusplus
}
#endif
