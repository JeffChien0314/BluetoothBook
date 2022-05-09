/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ev.dialer.telecom;

import android.content.Context;
import android.os.Handler;
import android.telecom.Call;

import com.ev.dialer.log.L;
import com.ev.dialer.notification.InCallNotificationController;

import java.util.ArrayList;

/**
 * Routes a call to different path depending on its state. If there is any {@link
 * InCallServiceImpl.ActiveCallListChangedCallback} that already handles the call, i.e. the {@link
 * InCallViewModel} that actively updates the in call page, then we don't show HUN for the ringing
 * call or attempt to start the in call page again.
 */
class InCallRouter {


}
