package com.richardkoster.fhictagenda.test;

import android.test.ActivityInstrumentationTestCase2;

import com.richardkoster.fhictagenda.LoginActivity;

import static org.fest.assertions.api.ANDROID.assertThat;


public class LoginTest extends ActivityInstrumentationTestCase2<LoginActivity> {
    public LoginTest() {
        super(LoginActivity.class);
    }


    public void testActionBarIsHidden() throws Exception {
        assertThat(getActivity().getActionBar()).isNull();
    }
}
