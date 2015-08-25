package org.magnum.dataup;

import org.magnum.dataup.model.Video;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Genius on 7/7/2015.
 */
@Repository
public interface VideoRepository  extends CrudRepository<Video,Long> {

     //Note: find videos with matching name throws exception find a fix
    //public Collection<Video> findByName(String name);

}
