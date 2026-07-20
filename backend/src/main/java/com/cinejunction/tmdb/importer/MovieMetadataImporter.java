package com.cinejunction.tmdb.importer;

import com.cinejunction.exception.MovieNotFoundException;
import com.cinejunction.movie.entity.Collection;
import com.cinejunction.movie.entity.Keyword;
import com.cinejunction.movie.entity.Movie;
import com.cinejunction.movie.entity.ProductionCompany;
import com.cinejunction.movie.entity.ProductionCountry;
import com.cinejunction.movie.entity.SpokenLanguage;
import com.cinejunction.movie.repository.CollectionRepository;
import com.cinejunction.movie.repository.KeywordRepository;
import com.cinejunction.movie.repository.MovieRepository;
import com.cinejunction.movie.repository.ProductionCompanyRepository;
import com.cinejunction.movie.repository.ProductionCountryRepository;
import com.cinejunction.movie.repository.SpokenLanguageRepository;
import com.cinejunction.tmdb.client.TMDbClient;
import com.cinejunction.tmdb.dto.CollectionDto;
import com.cinejunction.tmdb.dto.KeywordDto;
import com.cinejunction.tmdb.dto.MovieDetailsDto;
import com.cinejunction.tmdb.dto.ProductionCompanyDto;
import com.cinejunction.tmdb.dto.ProductionCountryDto;
import com.cinejunction.tmdb.dto.SpokenLanguageDto;
import com.cinejunction.tmdb.dto.ImportMovieMetadataResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MovieMetadataImporter {

    private static final Logger log = LoggerFactory.getLogger(MovieMetadataImporter.class);

    private final TMDbClient tmDbClient;
    private final MovieRepository movieRepository;
    private final ProductionCompanyRepository productionCompanyRepository;
    private final ProductionCountryRepository productionCountryRepository;
    private final SpokenLanguageRepository spokenLanguageRepository;
    private final KeywordRepository keywordRepository;
    private final CollectionRepository collectionRepository;

    public MovieMetadataImporter(TMDbClient tmDbClient, MovieRepository movieRepository,
                                 ProductionCompanyRepository productionCompanyRepository,
                                 ProductionCountryRepository productionCountryRepository,
                                 SpokenLanguageRepository spokenLanguageRepository,
                                 KeywordRepository keywordRepository,
                                 CollectionRepository collectionRepository) {
        this.tmDbClient = tmDbClient;
        this.movieRepository = movieRepository;
        this.productionCompanyRepository = productionCompanyRepository;
        this.productionCountryRepository = productionCountryRepository;
        this.spokenLanguageRepository = spokenLanguageRepository;
        this.keywordRepository = keywordRepository;
        this.collectionRepository = collectionRepository;
    }

    @Transactional
    public ImportMovieMetadataResponse importMetadata(Long tmdbMovieId) {
        log.info("Movie metadata import started for TMDb movie ID: {}", tmdbMovieId);

        MovieDetailsDto tmdbMovie = tmDbClient.getMovieById(tmdbMovieId);
        log.info("Fetched TMDb movie details for movie ID: {}", tmdbMovieId);

        Movie movie = movieRepository.findAllByTitleIgnoreCase(tmdbMovie.getTitle())
                .stream()
                .findFirst()
                .orElseThrow(() -> new MovieNotFoundException("Movie not found in local database. Import the movie first."));

        AtomicInteger companiesCreated = new AtomicInteger(0);
        AtomicInteger companiesReused = new AtomicInteger(0);
        AtomicInteger countriesCreated = new AtomicInteger(0);
        AtomicInteger languagesCreated = new AtomicInteger(0);
        AtomicInteger keywordsCreated = new AtomicInteger(0);
        AtomicInteger collectionCreated = new AtomicInteger(0);

        if (tmdbMovie.getProduction_companies() != null) {
            Set<ProductionCompany> companies = new HashSet<>();
            for (ProductionCompanyDto companyDto : tmdbMovie.getProduction_companies()) {
                ProductionCompany company = productionCompanyRepository.findByTmdbId(companyDto.getId())
                        .orElseGet(() -> {
                            ProductionCompany newCompany = new ProductionCompany();
                            newCompany.setTmdbId(companyDto.getId());
                            newCompany.setName(companyDto.getName());
                            newCompany.setLogoPath(companyDto.getLogo_path());
                            newCompany.setOriginCountry(companyDto.getOrigin_country());
                            ProductionCompany saved = productionCompanyRepository.save(newCompany);
                            companiesCreated.incrementAndGet();
                            log.info("Created production company: {}", saved.getName());
                            return saved;
                        });
                companies.add(company);
                if (company.getId() != null && productionCompanyRepository.existsByTmdbId(company.getTmdbId())) {
                    companiesReused.incrementAndGet();
                }
            }
            movie.getProductionCompanies().clear();
            movie.getProductionCompanies().addAll(companies);
        }

        if (tmdbMovie.getProduction_countries() != null) {
            Set<ProductionCountry> countries = new HashSet<>();
            for (ProductionCountryDto countryDto : tmdbMovie.getProduction_countries()) {
                ProductionCountry country = productionCountryRepository.findByIsoCode(countryDto.getIso_3166_1())
                        .orElseGet(() -> {
                            ProductionCountry newCountry = new ProductionCountry();
                            newCountry.setIsoCode(countryDto.getIso_3166_1());
                            newCountry.setName(countryDto.getName());
                            ProductionCountry saved = productionCountryRepository.save(newCountry);
                            countriesCreated.incrementAndGet();
                            log.info("Created production country: {}", saved.getName());
                            return saved;
                        });
                countries.add(country);
            }
            movie.getProductionCountries().clear();
            movie.getProductionCountries().addAll(countries);
        }

        if (tmdbMovie.getSpoken_languages() != null) {
            Set<SpokenLanguage> languages = new HashSet<>();
            for (SpokenLanguageDto languageDto : tmdbMovie.getSpoken_languages()) {
                SpokenLanguage language = spokenLanguageRepository.findByIso6391(languageDto.getIso_639_1())
                        .orElseGet(() -> {
                            SpokenLanguage newLanguage = new SpokenLanguage();
                            newLanguage.setIso6391(languageDto.getIso_639_1());
                            newLanguage.setEnglishName(languageDto.getEnglish_name());
                            newLanguage.setName(languageDto.getName());
                            SpokenLanguage saved = spokenLanguageRepository.save(newLanguage);
                            languagesCreated.incrementAndGet();
                            log.info("Created spoken language: {}", saved.getEnglishName());
                            return saved;
                        });
                languages.add(language);
            }
            movie.getSpokenLanguages().clear();
            movie.getSpokenLanguages().addAll(languages);
        }

        java.util.List<KeywordDto> keywords = tmDbClient.getMovieKeywords(tmdbMovieId);
        if (keywords != null && !keywords.isEmpty()) {
            Set<Keyword> keywordEntities = new HashSet<>();
            for (KeywordDto keywordDto : keywords) {
                Keyword keyword = keywordRepository.findByTmdbId(keywordDto.getId())
                        .orElseGet(() -> {
                            Keyword newKeyword = new Keyword();
                            newKeyword.setTmdbId(keywordDto.getId());
                            newKeyword.setName(keywordDto.getName());
                            Keyword saved = keywordRepository.save(newKeyword);
                            keywordsCreated.incrementAndGet();
                            log.info("Created keyword: {}", saved.getName());
                            return saved;
                        });
                keywordEntities.add(keyword);
            }
            movie.getKeywords().clear();
            movie.getKeywords().addAll(keywordEntities);
        }

        if (tmdbMovie.getBelongsToCollection() != null) {
            CollectionDto collectionDto = tmdbMovie.getBelongsToCollection();
            Collection collection = collectionRepository.findByTmdbId(collectionDto.getId())
                    .orElseGet(() -> {
                        Collection newCollection = new Collection();
                        newCollection.setTmdbId(collectionDto.getId());
                        newCollection.setName(collectionDto.getName());
                        newCollection.setPosterPath(collectionDto.getPoster_path());
                        newCollection.setBackdropPath(collectionDto.getBackdrop_path());
                        Collection saved = collectionRepository.save(newCollection);
                        collectionCreated.incrementAndGet();
                        log.info("Created collection: {}", saved.getName());
                        return saved;
                    });
            movie.setCollection(collection);
        }

        movieRepository.save(movie);

        log.info("Movie metadata import completed for movie ID: {}. Companies created: {}, reused: {}, countries created: {}, languages created: {}, keywords created: {}, collection created: {}",
                movie.getId(), companiesCreated.get(), companiesReused.get(), countriesCreated.get(), languagesCreated.get(), keywordsCreated.get(), collectionCreated.get());

        return ImportMovieMetadataResponse.builder()
                .movieId(movie.getId())
                .movieTitle(movie.getTitle())
                .companiesCreated(companiesCreated.get())
                .companiesReused(companiesReused.get())
                .countriesCreated(countriesCreated.get())
                .languagesCreated(languagesCreated.get())
                .keywordsCreated(keywordsCreated.get())
                .collectionCreated(collectionCreated.get() > 0)
                .message("Movie metadata imported successfully")
                .build();
    }
}
