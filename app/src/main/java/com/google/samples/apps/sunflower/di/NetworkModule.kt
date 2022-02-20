/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.sunflower.di

import com.google.samples.apps.sunflower.api.UnsplashService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// 각 모듈이 어떤 scope 에서 사용되는지 알려줌
@InstallIn(SingletonComponent::class)
// Hilt 에게 인스턴스를 제공하는 방법을 알려줌
@Module
class NetworkModule {

    // singleton 이라고 hilt 에게 알려줌
    @Singleton
    // Hilt 에게 어떻게 생성하는지 알려줌
    @Provides
    fun provideUnsplashService(): UnsplashService {
        // UnsplashService 를 생성하여 리턴
        return UnsplashService.create()
    }
}
