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
package org.magnum.dataup.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fluentinterface.ReflectionBuilder;
import com.fluentinterface.builder.Builder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
                    ___                    ___           ___                            
     _____         /\  \                  /\  \         /\  \                           
    /::\  \       /::\  \                 \:\  \       /::\  \         ___              
   /:/\:\  \     /:/\:\  \                 \:\  \     /:/\:\  \       /\__\             
  /:/  \:\__\   /:/  \:\  \            _____\:\  \   /:/  \:\  \     /:/  /             
 /:/__/ \:|__| /:/__/ \:\__\          /::::::::\__\ /:/__/ \:\__\   /:/__/              
 \:\  \ /:/  / \:\  \ /:/  /          \:\~~\~~\/__/ \:\  \ /:/  /  /::\  \              
  \:\  /:/  /   \:\  /:/  /            \:\  \        \:\  /:/  /  /:/\:\  \             
   \:\/:/  /     \:\/:/  /              \:\  \        \:\/:/  /   \/__\:\  \            
    \::/  /       \::/  /                \:\__\        \::/  /         \:\__\           
     \/__/         \/__/                  \/__/         \/__/           \/__/           
      ___           ___                                     ___                         
     /\  \         /\  \         _____                     /\__\                        
    |::\  \       /::\  \       /::\  \       ___         /:/ _/_         ___           
    |:|:\  \     /:/\:\  \     /:/\:\  \     /\__\       /:/ /\__\       /|  |          
  __|:|\:\  \   /:/  \:\  \   /:/  \:\__\   /:/__/      /:/ /:/  /      |:|  |          
 /::::|_\:\__\ /:/__/ \:\__\ /:/__/ \:|__| /::\  \     /:/_/:/  /       |:|  |          
 \:\~~\  \/__/ \:\  \ /:/  / \:\  \ /:/  / \/\:\  \__  \:\/:/  /      __|:|__|          
  \:\  \        \:\  /:/  /   \:\  /:/  /   ~~\:\/\__\  \::/__/      /::::\  \          
   \:\  \        \:\/:/  /     \:\/:/  /       \::/  /   \:\  \      ~~~~\:\  \         
    \:\__\        \::/  /       \::/  /        /:/  /     \:\__\          \:\__\        
     \/__/         \/__/         \/__/         \/__/       \/__/           \/__/        
 */
@Entity
public class Video {

	public static VideoBuilder create() {
		return ReflectionBuilder.implementationFor(VideoBuilder.class).create();
	}

 	@JsonIgnore
	public List<Double> getRatingList() {
		return ratingList;
	}


	public Video(){

	}
	/*@JsonIgnore
	public void setRatingList(List<Double> ratingList) {
		this.ratingList = ratingList;
	}
*/
	public interface VideoBuilder extends Builder<Video> {
		public VideoBuilder withTitle(String title);
		public VideoBuilder withDuration(long duration);
		public VideoBuilder withSubject(String subject);
		public VideoBuilder withContentType(String contentType);
	}

	@Id
	@GeneratedValue(strategy= GenerationType.AUTO)
	private long id;//innitialize the id
	private String title;
	private long duration;
	private String location;
	private String subject;
	private String contentType;
    private double rating=0d;

	@JsonIgnore
 	private  transient List<Double> ratingList=new LinkedList<>();

	@JsonIgnore
	private String dataUrl;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}


 	public void setRating(double rating) {
		this.rating = rating;
	}
    public double getRating() {
		double  sum=0d;
		if(ratingList.size()==0)return sum;
		for(Double d:ratingList){
			sum+=d;
		}
		this.setRating( sum/(double)ratingList.size());
		return rating;
	}

	@JsonIgnore
	public void addRating(double rating) {
		if (rating < 1 || rating > 5) {
			return;
		}
 		this.ratingList.add(rating);

	}

	@JsonProperty
	public String getDataUrl() {
		return dataUrl;
	}

	@JsonIgnore
	public void setDataUrl(String dataUrl) {
		this.dataUrl = dataUrl;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getTitle(), getDuration());
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Video)
				&& Objects.equals(getTitle(), ((Video) obj).getTitle())
				&& getDuration() == ((Video) obj).getDuration();
	}

}
