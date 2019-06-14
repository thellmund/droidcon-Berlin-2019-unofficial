package com.hellmund.droidcon2019.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.hellmund.droidcon2019.data.model.EventDay
import com.hellmund.droidcon2019.data.model.Level
import com.hellmund.droidcon2019.data.model.Stage
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import java.lang.reflect.Type

typealias EventType = com.hellmund.droidcon2019.data.model.Type

object EventSerializer {

    val gson: Gson by lazy {
        GsonBuilder()
            .registerTypeAdapter(
                LocalTime::class.java,
                object : JsonDeserializer<LocalTime> {
                    private val formatter = DateTimeFormatter.ISO_LOCAL_TIME
                    override fun deserialize(
                        json: JsonElement,
                        typeOfT: Type,
                        context: JsonDeserializationContext
                    ): LocalTime = LocalTime.from(formatter.parse(json.asString))
                }
            )
            .registerTypeAdapter(
                EventDay::class.java,
                object : JsonDeserializer<EventDay> {
                    override fun deserialize(
                        json: JsonElement,
                        typeOfT: Type,
                        context: JsonDeserializationContext
                    ): EventDay = EventDay.valueOf(json.asString)
                }
            )
            .registerTypeAdapter(
                Stage::class.java,
                object : JsonDeserializer<Stage> {
                    override fun deserialize(
                        json: JsonElement,
                        typeOfT: Type,
                        context: JsonDeserializationContext
                    ): Stage {
                        return try {
                            Stage.valueOf(json.asString)
                        } catch (e: Exception) {
                            Stage.None
                        }
                    }
                }
            )
            .registerTypeAdapter(
                EventType::class.java,
                object : JsonDeserializer<EventType> {
                    override fun deserialize(
                        json: JsonElement,
                        typeOfT: Type,
                        context: JsonDeserializationContext
                    ): EventType {
                        return when (json.asString) {
                            "Session" -> EventType.Session
                            "Lightning talk" -> EventType.LightningTalk
                            "Workshop" -> EventType.Workshop
                            "Panel Discussion" -> EventType.PanelDiscussion
                            else -> EventType.None
                        }
                    }
                }
            )
            .registerTypeAdapter(
                Level::class.java,
                object : JsonDeserializer<Level> {
                    override fun deserialize(
                        json: JsonElement,
                        typeOfT: Type,
                        context: JsonDeserializationContext
                    ): Level {
                        return when (json.asString) {
                            "Introductory and overview" -> Level.Introductory
                            "Intermediate" -> Level.Intermediate
                            "Advanced" -> Level.Advanced
                            else -> Level.None
                        }
                    }
                }
            )
            .create()
    }

}
