/*
 * 
 * Copyright 2014 Jules White
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.magnum.dataup;

import org.magnum.dataup.model.Video;
import org.magnum.dataup.model.VideoStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

@Controller
public class VideoServiceController {

     @Autowired
    VideoRepository videos;

    @Autowired
    VideoFileManager mVideoFileManager;

    @RequestMapping(value = VideoSvcApi.VIDEO_SVC_PATH, method = RequestMethod.GET)
    public @ResponseBody Collection<Video> getVideoList() {
        //use reponse body so as to get a application/json as the responseType
        return (Collection<Video>) videos.findAll();
    }

    @RequestMapping(value = VideoSvcApi.VIDEO_SVC_PATH, method = RequestMethod.POST)
    public  @ResponseBody Video addVideo(@RequestBody Video v, HttpServletResponse response) {
        //set the status to 200 or 400,415 depending on whether the video was added
        //save and update the record in the repository
        Video savedVideo = videos.save(v);
        savedVideo.setDataUrl(getDataUrl(savedVideo.getId()));
        response.setStatus(videos.save(savedVideo) != null ? HttpServletResponse.SC_OK :
                HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
        return v;
    }

    @RequestMapping(value = VideoSvcApi.VIDEO_DATA_PATH, method = RequestMethod.POST)
    public @ResponseBody VideoStatus setVideoData(@PathVariable(VideoSvcApi.ID_PARAMETER) long id,//path variable id
                             @RequestParam(VideoSvcApi.DATA_PARAMETER) MultipartFile videoData,// the data(part video
                             HttpServletResponse response) {//the response from the operation
        //set the response header and other information like status
        response.setContentType("application/json"); //  OR //response.addHeader("Content-Type","application/json");
        response.setStatus(getDataStatus(id));

        VideoStatus videoStatus = new VideoStatus(VideoStatus.VideoState.NO_STATUS);//innitialize the video status

        if (response.getStatus() == HttpServletResponse.SC_OK) {//check if the video exist
            try {
                videoStatus.setState(VideoStatus.VideoState.PROCESSING);//transit to processing state
                Video video = videos.findOne(id);//get the video
                mVideoFileManager.saveVideoData(video, videoData.getInputStream());
                response.flushBuffer();//write the result to the client
                videoStatus.setState(VideoStatus.VideoState.READY);//transit to ready state
            } catch (IOException ex) {
                ex.printStackTrace();
                videoStatus.setState(VideoStatus.VideoState.NO_STATUS);//jjust in case exception occurs, return default
            }
        }
        return videoStatus; //return the video status. please note that the NO_STATUS will be returned if somethig bad happened
    }


    public int getDataStatus(final long id) {
        Video video = videos.findOne(id);
        if (video == null)
            return HttpServletResponse.SC_NOT_FOUND;//return 400
        return HttpServletResponse.SC_OK;//return 200 ok
    }

    @RequestMapping(value = VideoSvcApi.VIDEO_DATA_PATH, method = RequestMethod.GET)
    public void getData(@PathVariable(VideoSvcApi.ID_PARAMETER) long id, HttpServletResponse response) throws IOException {

        if (getDataStatus(id) == HttpServletResponse.SC_OK) { //if you can assert that the video metadata exist then we can retrieve it
            Video video = videos.findOne(id);//get the video
            response.setContentType("application/json");//send the response in json format
            //response.setBufferSize(150);//set the buffer size to 150mb
            //use closable printweiter to write and flush the result and display in firm of
            //copy the actual data
            mVideoFileManager.copyVideoData(video, response.getOutputStream());//write the result/data to the responses output stream to

            //print the metaData for the video as json
            try (PrintWriter writer = response.getWriter()) {
                writer.print(video);//write the result
                writer.flush();//flush
            } finally {
                response.setStatus(HttpServletResponse.SC_OK);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }

    }

    /**
     * This Rest method sets and get an average rating for a video whose id is passed
     *
     * @param id       of the video for which the rating is to be identified
     * @param rating   to pass
     * @param response of the status of the operation
     * @return
     */
    @RequestMapping(value = VideoSvcApi.VIDEO_RATING, method = RequestMethod.POST)
    public @ResponseBody double setAndGetRatingForVideo(@PathVariable(VideoSvcApi.ID_PARAMETER) long id, @PathVariable(VideoSvcApi.ID_RATING) double rating, HttpServletResponse response) throws IOException {
        if (getDataStatus(id) == HttpServletResponse.SC_OK) { //if you can assert that the video metadata exist then we can retrieve it
            Video video = videos.findOne(id);//get the video
            video.setRating(rating);
            System.out.println("added rating:"+rating);
            response.setStatus(HttpServletResponse.SC_OK);
            System.out.println("returning rating"+video.getRating());
            return video.getRating();
        }
        response.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);//return a 417 error
        return -1d;//something bad happened
    }


    private String getDataUrl(long videoId) {
        System.out.println(" innet address:"+Utils.getIpUsingInetAddress());

        String url = Utils.getIpUsingInetAddress() + "/video/" + videoId + "/data";
        return url;
    }


}
