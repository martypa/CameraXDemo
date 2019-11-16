package com.example.cameraxdemo.ui.capture

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CaptureViewModel : BaseObservable() {

   var switchFlashState: Boolean
   @Bindable get(): Boolean{
      return switchFlashState
   } set(value:Boolean){
      switchFlashState = value
   }

   var switchLensState: Boolean
   @Bindable get(): Boolean{
      return switchLensState
   } set(value:Boolean){
      switchLensState = value
   }
}