//package com.angcyo.spring.base.json
//
//import com.angcyo.spring.util.Constant
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
//import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
//import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
//import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
//import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.core.convert.converter.Converter
//import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
//import java.time.LocalDate
//import java.time.LocalDateTime
//import java.time.format.DateTimeFormatter
//
///**
// *
// * https://blog.csdn.net/WeiHao0240/article/details/100739760
// *
// * Email:angcyo@126.com
// * @author angcyo
// * @date 2021-6-18
// */
//@Configuration
//class LocalDateTimeSerializerConfig {
//
//    /**
//     * string转localdate
//     */
//    @Bean
//    fun localDateConverter(): Converter<String, LocalDate> {
//        return Converter<String, LocalDate> { source ->
//            if (source.trim { it <= ' ' }.isEmpty()) return@Converter null
//            try {
//                return@Converter LocalDate.parse(source)
//            } catch (e: Exception) {
//                return@Converter LocalDate.parse(source, DateTimeFormatter.ofPattern(Constant.DEFAULT_DATE_FORMATTER))
//            }
//        }
//    }
//
//    /**
//     * string转localdatetime
//     */
//    @Bean
//    fun localDateTimeConverter(): Converter<String, LocalDateTime> {
//        return Converter<String, LocalDateTime> { source ->
//            if (source.trim { it <= ' ' }.isEmpty()) return@Converter null
//            // 先尝试ISO格式: 2019-07-15T16:00:00
//            try {
//                return@Converter LocalDateTime.parse(source)
//            } catch (e: Exception) {
//                return@Converter LocalDateTime.parse(
//                    source,
//                    DateTimeFormatter.ofPattern(Constant.DEFAULT_DATE_TIME_FORMATTER)
//                )
//            }
//        }
//    }
//
//    /**
//     * 统一配置
//     */
//    @Bean
//    fun jsonCustomizer(): Jackson2ObjectMapperBuilderCustomizer {
//        val module = JavaTimeModule()
//        val localDateTimeDeserializer =
//            LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(Constant.DEFAULT_DATE_TIME_FORMATTER))
//        module.addDeserializer(LocalDateTime::class.java, localDateTimeDeserializer)
//        return Jackson2ObjectMapperBuilderCustomizer { builder: Jackson2ObjectMapperBuilder ->
//            builder.simpleDateFormat(Constant.DEFAULT_DATE_TIME_FORMATTER)
//            builder.serializers(LocalDateSerializer(DateTimeFormatter.ofPattern(Constant.DEFAULT_DATE_FORMATTER)))
//            builder.serializers(LocalDateTimeSerializer(DateTimeFormatter.ofPattern(Constant.DEFAULT_DATE_TIME_FORMATTER)))
//            builder.modules(module)
//        }
//    }
//}