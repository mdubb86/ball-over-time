//package com.ballovertime.datagen.service;
//
//import com.ballovertime.datagen.download.SeasonDownloader;
//import com.ballovertime.datagen.model.SeasonSegment;
//import jakarta.inject.Inject;
//import jakarta.inject.Singleton;
//
//import java.time.LocalDate;
//import java.time.Year;
//import java.util.List;
//import java.util.Optional;
//
//@Singleton
//public class SeasonService {
//
//    private final SeasonDownloader downloader;
//
//    @Inject
//    public SeasonService(SeasonDownloader downloader) {
//        this.downloader = downloader;
//    }
//
//    public void synchronize(int startYear) {
//
//        for (int year = startYear; year<= Year.now().getValue(); year++) {
//
//            List<SeasonSegment> seasonSegments = downloader.lookupSegments(year);
//
//            // Check for overlaps
//            for (SeasonSegment seasonSegment : seasonSegments) {
//
//                Optional<SeasonSegment> overlapping = seasonSegments.stream()
//                        .filter(ss -> overlaps(seasonSegment, ss))
//                        .findAny();
//
//                if (overlapping.isPresent()) {
//                    throw new RuntimeException(String.format("Overlapping season segments detected: %s <-> %s",
//                            seasonSegment, overlapping.get()));
//                }
//            }
//
//        }
//    }
//
//    private boolean overlaps(SeasonSegment s1, SeasonSegment s2) {
//        if (s1 == s2) {
//            return false;
//        }
//        return overlaps(s1.start(), s1.end(), s2.start()) ||
//                overlaps(s1.start(), s1.end(), s2.end());
//    }
//
//
//    private boolean overlaps(LocalDate start, LocalDate end, LocalDate target) {
//        return start.isEqual(target) || end.isEqual(target) || (start.isBefore(target) && end.isAfter(target));
//    }
//
//}
