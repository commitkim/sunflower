/*
 * Copyright 2018 Google LLC
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

package com.google.samples.apps.sunflower.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.platform.app.InstrumentationRegistry
import com.google.samples.apps.sunflower.utilities.testCalendar
import com.google.samples.apps.sunflower.utilities.testGardenPlanting
import com.google.samples.apps.sunflower.utilities.testPlant
import com.google.samples.apps.sunflower.utilities.testPlants
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GardenPlantingDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var gardenPlantingDao: GardenPlantingDao
    private var testGardenPlantingId: Long = 0

    // 모두 한 스레드에서 실행되게 하여 동기화를 생각하지 않을 수 있다.
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    // before 로 db 생성
    @Before fun createDb() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        gardenPlantingDao = database.gardenPlantingDao()

        // test에 필요한 데이터들을 insert
        database.plantDao().insertAll(testPlants)
        // test에 필요한 데이터들을 insert
        testGardenPlantingId = gardenPlantingDao.insertGardenPlanting(testGardenPlanting)
    }

    // after 로 db close
    @After fun closeDb() {
        database.close()
    }

    // GardenPlanting 를 하나 추가하고 잘 추가 되었는지 확인
    @Test fun testGetGardenPlantings() = runBlocking {
        val gardenPlanting2 = GardenPlanting(
            testPlants[1].plantId,
            testCalendar,
            testCalendar
        ).also { it.gardenPlantingId = 2 }
        gardenPlantingDao.insertGardenPlanting(gardenPlanting2)
        assertThat(gardenPlantingDao.getGardenPlantings().first().size, equalTo(2))
    }

    // GardenPlanting 를 하나 추가하고 잘 들어갔는지 확인후 삭제하여 잘 삭제되어 기존에 있던거가 남아있는지 확인
    @Test fun testDeleteGardenPlanting() = runBlocking {
        val gardenPlanting2 = GardenPlanting(
            testPlants[1].plantId,
            testCalendar,
            testCalendar
        ).also { it.gardenPlantingId = 2 }
        gardenPlantingDao.insertGardenPlanting(gardenPlanting2)
        assertThat(gardenPlantingDao.getGardenPlantings().first().size, equalTo(2))
        gardenPlantingDao.deleteGardenPlanting(gardenPlanting2)
        assertThat(gardenPlantingDao.getGardenPlantings().first().size, equalTo(1))
    }

    // 기존에 들어가있던 plantId로 plantId가 있는지 확인하는 isPlanted()가 ture 잘 동작하는지 확인
    @Test fun testGetGardenPlantingForPlant() = runBlocking {
        assertTrue(gardenPlantingDao.isPlanted(testPlant.plantId).first())
    }

    // 들어가있지 않은 plantId로 plantId가 있는지 확인하는 isPlanted()가 false 잘 동작하는지 확인
    @Test fun testGetGardenPlantingForPlant_notFound() = runBlocking {
        assertFalse(gardenPlantingDao.isPlanted(testPlants[2].plantId).first())
    }

    // 기존에 들어가 있던게 잘 들어가있는지 확인
    @Test fun testGetPlantAndGardenPlantings() = runBlocking {
        val plantAndGardenPlantings = gardenPlantingDao.getPlantedGardens().first()
        assertThat(plantAndGardenPlantings.size, equalTo(1))

        /**
         * Only the [testPlant] has been planted, and thus has an associated [GardenPlanting]
         */
        assertThat(plantAndGardenPlantings[0].plant, equalTo(testPlant))
        assertThat(plantAndGardenPlantings[0].gardenPlantings.size, equalTo(1))
        assertThat(plantAndGardenPlantings[0].gardenPlantings[0], equalTo(testGardenPlanting))
    }
}
