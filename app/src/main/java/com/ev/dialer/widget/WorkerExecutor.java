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

package com.ev.dialer.widget;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * WorkerExecutor is a singleton tied to the application to provide {@link ExecutorService} for
 * Dialer to run tasks in background.
 */
public class WorkerExecutor {
    private static WorkerExecutor sWorkerExecutor;

    private ExecutorService mSingleThreadExecutor;

    /** Returns the singleton WorkerExecutor for the application. */
    public static WorkerExecutor getInstance() {
        if (sWorkerExecutor == null) {
            sWorkerExecutor = new WorkerExecutor();
        }
        return sWorkerExecutor;
    }

    private WorkerExecutor() {
        mSingleThreadExecutor = Executors.newSingleThreadExecutor();
    }

    /** Returns the single thread executor. */
    public ExecutorService getSingleThreadExecutor() {
        return mSingleThreadExecutor;
    }

    /** Tears down the singleton WorkerExecutor for the application */
    public void tearDown() {
        mSingleThreadExecutor.shutdown();
        sWorkerExecutor = null;
    }
}
