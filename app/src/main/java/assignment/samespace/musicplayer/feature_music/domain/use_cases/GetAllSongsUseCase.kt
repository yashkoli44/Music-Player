package assignment.samespace.musicplayer.feature_music.domain.use_cases

import assignment.samespace.musicplayer.feature_music.domain.model.Song
import assignment.samespace.musicplayer.feature_music.domain.repository.SongRepository

class GetAllSongsUseCase(
    private val songRepository: SongRepository
) {

    suspend operator fun invoke(): List<Song>{
        return songRepository.getAllSongs()
    }


}