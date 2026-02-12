package com.example.demo.config;

import com.example.demo.entity.Category;
import com.example.demo.entity.Organization;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.enums.PlanType;
import com.example.demo.enums.Role;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.OrganizationRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

/**
 * Data Initialization Configuration
 * Creates sample data on application startup for testing/demo purposes
 * 
 * IMPORTANT: Remove or disable this in production!
 */
@Configuration
public class DataInitializer {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initDatabase(
            OrganizationRepository organizationRepository,
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            ProductRepository productRepository) {

        return args -> {
            // Check if data already exists
            if (organizationRepository.count() > 0) {
                System.out.println("Database already initialized. Skipping data initialization.");
                return;
            }

            System.out.println("Initializing database with sample data...");

            // Create Organizations
            Organization org1 = Organization.builder()
                    .name("TechStore Inc.")
                    .companyCode("TECH001")
                    .description("Technology retail company")
                    .planType(PlanType.PREMIUM)
                    .isActive(true)
                    .maxUsers(50)
                    .maxProducts(1000)
                    .build();
            organizationRepository.save(org1);

            Organization org2 = Organization.builder()
                    .name("Fashion Hub Ltd.")
                    .companyCode("FASH001")
                    .description("Fashion and apparel retailer")
                    .planType(PlanType.BASIC)
                    .isActive(true)
                    .maxUsers(20)
                    .maxProducts(500)
                    .build();
            organizationRepository.save(org2);

            // Create Users for Organization 1
            User admin1 = User.builder()
                    .username("admin")
                    .email("admin@techstore.com")
                    .password(passwordEncoder.encode("admin123"))
                    .firstName("John")
                    .lastName("Admin")
                    .role(Role.ADMIN)
                    .organization(org1)
                    .isActive(true)
                    .phoneNumber("+1234567890")
                    .build();
            userRepository.save(admin1);

            User staff1 = User.builder()
                    .username("staff")
                    .email("staff@techstore.com")
                    .password(passwordEncoder.encode("staff123"))
                    .firstName("Jane")
                    .lastName("Staff")
                    .role(Role.STAFF)
                    .organization(org1)
                    .isActive(true)
                    .phoneNumber("+1234567891")
                    .build();
            userRepository.save(staff1);

            // Create Users for Organization 2
            User admin2 = User.builder()
                    .username("admin2")
                    .email("admin@fashionhub.com")
                    .password(passwordEncoder.encode("admin123"))
                    .firstName("Mike")
                    .lastName("Admin")
                    .role(Role.ADMIN)
                    .organization(org2)
                    .isActive(true)
                    .build();
            userRepository.save(admin2);

            // Create SUPER_ADMIN (can manage all organizations)
            Organization superOrg = Organization.builder()
                    .name("IMS Platform")
                    .companyCode("IMS001")
                    .description("Platform administration")
                    .planType(PlanType.ENTERPRISE)
                    .isActive(true)
                    .build();
            organizationRepository.save(superOrg);

            User superAdmin = User.builder()
                    .username("superadmin")
                    .email("superadmin@ims.com")
                    .password(passwordEncoder.encode("super123"))
                    .firstName("Super")
                    .lastName("Admin")
                    .role(Role.SUPER_ADMIN)
                    .organization(superOrg)
                    .isActive(true)
                    .build();
            userRepository.save(superAdmin);

            // Create Categories for Organization 1 (TechStore)
            Category laptops = Category.builder()
                    .name("Laptops")
                    .description("Laptop computers and accessories")
                    .organization(org1)
                    .isActive(true)
                    .build();
            categoryRepository.save(laptops);

            Category phones = Category.builder()
                    .name("Smartphones")
                    .description("Mobile phones and accessories")
                    .organization(org1)
                    .isActive(true)
                    .build();
            categoryRepository.save(phones);

            Category accessories = Category.builder()
                    .name("Accessories")
                    .description("Tech accessories")
                    .organization(org1)
                    .isActive(true)
                    .build();
            categoryRepository.save(accessories);

            // Create Products for Organization 1
            Product laptop1 = Product.builder()
                    .name("MacBook Pro 16\"")
                    .sku("TECH-LAP-001")
                    .description("High-performance laptop with M3 chip")
                    .price(new BigDecimal("2499.99"))
                    .stockQuantity(15)
                    .lowStockThreshold(5)
                    .category(laptops)
                    .organization(org1)
                    .isActive(true)
                    .build();
            productRepository.save(laptop1);

            Product laptop2 = Product.builder()
                    .name("Dell XPS 15")
                    .sku("TECH-LAP-002")
                    .description("Premium Windows laptop")
                    .price(new BigDecimal("1899.99"))
                    .stockQuantity(20)
                    .lowStockThreshold(5)
                    .category(laptops)
                    .organization(org1)
                    .isActive(true)
                    .build();
            productRepository.save(laptop2);

            Product phone1 = Product.builder()
                    .name("iPhone 15 Pro")
                    .sku("TECH-PHN-001")
                    .description("Latest iPhone with A17 chip")
                    .price(new BigDecimal("999.99"))
                    .stockQuantity(3) // Low stock
                    .lowStockThreshold(5)
                    .category(phones)
                    .organization(org1)
                    .isActive(true)
                    .build();
            productRepository.save(phone1);

            Product accessory1 = Product.builder()
                    .name("Wireless Mouse")
                    .sku("TECH-ACC-001")
                    .description("Ergonomic wireless mouse")
                    .price(new BigDecimal("49.99"))
                    .stockQuantity(100)
                    .lowStockThreshold(20)
                    .category(accessories)
                    .organization(org1)
                    .isActive(true)
                    .build();
            productRepository.save(accessory1);

            // Create Categories for Organization 2 (Fashion Hub)
            Category mensClothing = Category.builder()
                    .name("Men's Clothing")
                    .description("Men's fashion and apparel")
                    .organization(org2)
                    .isActive(true)
                    .build();
            categoryRepository.save(mensClothing);

            Category womensClothing = Category.builder()
                    .name("Women's Clothing")
                    .description("Women's fashion and apparel")
                    .organization(org2)
                    .isActive(true)
                    .build();
            categoryRepository.save(womensClothing);

            // Create Products for Organization 2
            Product shirt1 = Product.builder()
                    .name("Men's Cotton T-Shirt")
                    .sku("FASH-MEN-001")
                    .description("Comfortable cotton t-shirt")
                    .price(new BigDecimal("29.99"))
                    .stockQuantity(50)
                    .lowStockThreshold(10)
                    .category(mensClothing)
                    .organization(org2)
                    .isActive(true)
                    .build();
            productRepository.save(shirt1);

            Product dress1 = Product.builder()
                    .name("Women's Summer Dress")
                    .sku("FASH-WOM-001")
                    .description("Elegant summer dress")
                    .price(new BigDecimal("79.99"))
                    .stockQuantity(30)
                    .lowStockThreshold(10)
                    .category(womensClothing)
                    .organization(org2)
                    .isActive(true)
                    .build();
            productRepository.save(dress1);

            System.out.println("✅ Database initialized successfully!");
            System.out.println("\n=== Login Credentials ===");
            System.out.println("Organization 1 (TechStore Inc.):");
            System.out.println("  Admin: admin / admin123");
            System.out.println("  Staff: staff / staff123");
            System.out.println("\nOrganization 2 (Fashion Hub Ltd.):");
            System.out.println("  Admin: admin2 / admin123");
            System.out.println("\nSuper Admin:");
            System.out.println("  superadmin / super123");
            System.out.println("========================\n");
        };
    }
}
