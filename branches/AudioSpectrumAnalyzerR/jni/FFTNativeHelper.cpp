#include "FFTNativeHelper.hh"

FFTNativeHelper::FFTNativeHelper(double sampleRate, int numberOfFFTPoints) {
	mSampleRateInHz = sampleRate;
	mNumberOfFFTPoints = numberOfFFTPoints;
	re = new double[numberOfFFTPoints];
	im = new double[numberOfFFTPoints];
	mAbsSignal = new double[numberOfFFTPoints/2];
	mFFT = new Fft();
    int pow2 = mFFT->fftPow2FromWindowSize(numberOfFFTPoints);
    mFFT->fftInit(pow2);
}

FFTNativeHelper::FFTNativeHelper() {
	mSampleRateInHz = Constants.SAMPLING_FREQUENCY;
	mNumberOfFFTPoints = Constants.NUMBER_OF_FFT_POINTS;
	re = new double[numberOfFFTPoints];
	im = new double[numberOfFFTPoints];
	mAbsSignal = new double[Constants.NUMBER_OF_FFT_POINTS/2];
	mFFT = new Fft();
    int pow2 = mFFT->fftPow2FromWindowSize(numberOfFFTPoints);
    mFFT->fftInit(pow2);
}

FFTNativeHelper::~FFTNativeHelper() {
	if(re)
		delete [] re;
	if(im)
		delete [] im;
	if(mAbsSignal)
		delete [] mAbsSignal;
	if(mFFT)
		delete mFFT;

	re = 0;
	im = 0;
	mAbsSignal = 0;
	mFFT = 0;
}

double* FFTNativeHelper::calculateFFT(char *signal, int numberOfReadBytes) {
	double temp;

	for(int i = 0; i < mNumberOfFFTPoints; i++) {
		if((2*i+1) < numberOfReadBytes) {
			temp = (double)((signal[2*i] & 0xFF) | (signal[2*i+1] << 8)) / 32768.0F;
			re[i] = temp;
			im[i] = 0.0;
		} else {
			re[i] = 0.0;
			im[i] = 0.0;
		}
	}

	mFFT->fft(re,im);

	mMaxFFTSample = 0.0;
	mPeakPos = 0;
	for(int i = 0; i < (mNumberOfFFTPoints/2); i++) {
		mAbsSignal[i] = sqrt(pow(re[i], 2) + pow(im[i], 2));
		if(mAbsSignal[i] > mMaxFFTSample) {
			mMaxFFTSample = mAbsSignal[i];
			mPeakPos = i;
		}
	}

	return mAbsSignal;
}

double FFTNativeHelper::getPeakFrequency() {
	mPeakFreq = mPeakPos*(mSampleRateInHz/mNumberOfFFTPoints);
	return mPeakFreq;
}

int FFTNativeHelper::getPeakFrequencyPosition() {
	return mPeakPos;
}

double FFTNativeHelper::getPeakFrequency(int *absSignal) {

	int peakPos = 0, max = absSignal[0];

	for(int i=1; i < (mNumberOfFFTPoints/2); i++) {
		if(absSignal[i] > max) {
			max = absSignal[i];
			peakPos = i;
		}
	}

	return peakPos*(mSampleRateInHz/mNumberOfFFTPoints);
}

double FFTNativeHelper::getMaxFFTSample() {
	return mMaxFFTSample;
}

void FFTNativeHelper::setSamplingRate(double sampleRate) {
	mSampleRateInHz = sampleRate;
}

void FFTNativeHelper::setNumberOfFFTPoints(int numberOfFFTPoints) {
	mNumberOfFFTPoints = numberOfFFTPoints;
	mComplexSignal = new Complex[numberOfFFTPoints];
	mAbsSignal = new double[numberOfFFTPoints/2];
}

int FFTNativeHelper::getFFTSize() {
	return mNumberOfFFTPoints;
}
