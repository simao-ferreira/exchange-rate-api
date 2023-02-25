package io.template.app.infrastructure.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import javax.sql.DataSource

@Configuration
class JdbcConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "spring.pdb.datasource")
    fun pgDataSource(): DataSource {
        return DataSourceBuilder.create().build()
    }


    @Bean("pgJdbcTemplate")
    fun jdbcTemplate(@Qualifier("pgDataSource") dataSource: DataSource): NamedParameterJdbcTemplate {
        return NamedParameterJdbcTemplate(dataSource)
    }
}
