# Body Tapping Phone

## INTRODUCTION

This is an application to trigger a smart phone from body tapping. This idea is based on [Bootstrapping](http://dl.acm.org/citation.cfm?id=2984541) which was presented at UIST'16, but the algorithm used in my application is different from that of Bootstrapping in the following points:

1. My application uses NMF(Nonnegative Matrix Factorization) for machine learning but not SVM(Support Vector Machine) which is used at Bootstrapping. If you don't know NMF, I believe [this link](https://github.com/drumichiro/nmf-and-ntf) is useful to study the technique.
2. Time series data has not been dealt with in my application yet. Note that Bootstrapping tried dealing by inputting an array of time series data directly but this approach may be inappropriate, because it ignores the stretch property of time series data. A proper model to consider time series (e.g., HMM(Hidden Markov Model), RNN(Recurrent Neural Network) etc.) should be used to extract features.
3. Only online data (local data) has been used in my application yet. Though, note that applying prior distribution may be more suitable for my application because NMF is a kind of probabilistic model.

Regardless of the above differences, it looks just to me like my application is working to recognize body tapping well. If this application and source code is useful for your study, I will be happy.

## HOW TO USE

### Building application
I confirmed this application built on Android Studio is working. Aside from Android Studio, I believe any special requirements to build are not necessary.

### Usage of application
Let me explain steps and screenshots corresponding to the numbers.

1. This is shown at the beginning of application. Please press top right side.
2. Please press the shown index list of "Position*", in which "Position1", "Position2" and "Position3" are corresponding to <font color="Red">RED</font>, <font color="Green">GREEN</font> and <font color="Blue">BLUE</font>, respectively.
3. After a colored screen is shown, please tap a display side of your smart phone to somewhere on your body for registration (e.g., EAR, CHEST, THIGH etc.).
4. A white screen is shown after the registration. Until you register your body positions to all "Position*"s, please carry on the registration as with step 2 and step 3.
5. Please tap a registered position of your body. You can see a corresponding color.

|1. Beginning |2. Select "Position*" |3. registration |4. Select next "Position*" |5. Recognition |
|---|---|---|---|---|
|<img src=image/Screenshot_beginning.png width=256px> |<img src=image/Screenshot_registration_start.png width=256px> |<img src=image/Screenshot_registration_color.png width=1024px> |<img src=image/Screenshot_registration_done.png width=256px> |<img src=image/Screenshot_recognition_color.png width=1024px> |

Note that an E-mail icon on right bottom is unused here. This is attached in Android Studio's sample which my application is based on.

## REFERENCE
- Xiang 'Anthony' Chen et al.: _Bootstrapping User-Defined Body Tapping Recognition with Offline-Learned Probabilistic Representation_
