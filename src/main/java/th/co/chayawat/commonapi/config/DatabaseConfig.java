package th.co.chayawat.commonapi.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration(proxyBeanMethods = false)
@EnableTransactionManagement
// 1. เพิ่มแพ็กเกจ Repository ของคุณลงไปใน basePackages ด้วย เพื่อให้ Spring สแกนเจอ UserRepository ของคุณ
@EnableJpaRepositories(
        basePackages = {
                "th.co.truecorp.commonlib.jpa.repository",
                "th.co.chayawat.commonapi.cms.jpa.repository" // <-- เพิ่มบรรทัดนี้เข้ามา
        },
        entityManagerFactoryRef = "entityManagerFactory", // <-- 2. เปลี่ยนชื่อ Ref ให้เป็นชื่อมาตรฐาน
        transactionManagerRef = "transactionManager"       // <-- 3. เปลี่ยนชื่อ Ref ให้เป็นชื่อมาตรฐาน
)
public class DatabaseConfig {

    @Primary
    @Bean(name = "commonDatasource")
    @ConfigurationProperties("spring.datasource.db1")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder,
                                                                       @Qualifier("commonDatasource") DataSource dataSource) {
        return builder.dataSource(dataSource)
                // แก้ตรงนี้: เพิ่มแพ็กเกจ Entity ของทรูเข้าไปในรายการสแกนด้วย (คั่นด้วยเครื่องหมายจุลภาค ,)
                .packages(
                        "th.co.chayawat.commonapi.cms.jpa.entity",
                        "th.co.truecorp.commonlib.jpa.entity" // <-- เพิ่มบรรทัดนี้เข้ามาครับ
                )
                .persistenceUnit("commonDatasource")
                .build();
    }

    @Primary
    // 5. เปลี่ยนชื่อ Bean จาก "commonTransactionManager" เป็น "transactionManager"
    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) { // <-- เปลี่ยนตรง Qualifier นี้ด้วย
        return new JpaTransactionManager(entityManagerFactory);
    }

}