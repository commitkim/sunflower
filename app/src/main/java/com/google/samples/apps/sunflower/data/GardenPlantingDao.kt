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

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * The Data Access Object for the [GardenPlanting] class.
 */

// Room 의 Dao 로 선언
@Dao
interface GardenPlantingDao {

    // garden_plantings 에서 데이터를 모두 뽑아 Flow 로 전달
    @Query("SELECT * FROM garden_plantings")
    fun getGardenPlantings(): Flow<List<GardenPlanting>>

    // garden_plantings에서 plant_id 가 있는지 확인 하여 Flow 로 전달
    @Query("SELECT EXISTS(SELECT 1 FROM garden_plantings WHERE plant_id = :plantId LIMIT 1)")
    fun isPlanted(plantId: String): Flow<Boolean>

    /**
     * This query will tell Room to query both the [Plant] and [GardenPlanting] tables and handle
     * the object mapping.
     */
    // garden_plantings에서 plant_id 와 일치하는 id 를 가진 값을 plants 에서 뽑아 PlantAndGardenPlantings 로 변경하여 Flow로 전달
    // Transaction annotation 을 사용해서 한번의 transaction 으로 실행하여 일관성을 유지할 수 있다.
    @Transaction
    @Query("SELECT * FROM plants WHERE id IN (SELECT DISTINCT(plant_id) FROM garden_plantings)")
    fun getPlantedGardens(): Flow<List<PlantAndGardenPlantings>>

    // GardenPlanting 를 Insert
    @Insert
    suspend fun insertGardenPlanting(gardenPlanting: GardenPlanting): Long

    // GardenPlanting 를 Delete
    @Delete
    suspend fun deleteGardenPlanting(gardenPlanting: GardenPlanting)
}
