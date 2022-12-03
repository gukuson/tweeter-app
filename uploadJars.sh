#!/bin/bash
arr=(
        "getStory"
        "getFollwersCount"
        "getFollowers"
        "unfollow"
        "register"
	      "getFollowingCount"
	"getFollowing"
	"follow"
	"postStatus"
	"logout"
	"login"
	"getFeed"
	"isFollower"
	"getUser"
	"followerFetcher"
	"processFeedBatches"
    )
for FUNCTION_NAME in "${arr[@]}"
do
  aws lambda update-function-code --function-name $FUNCTION_NAME --zip-file fileb:///Users/stantonanthony/Desktop/CS_340/tweeter-new-main/server/build/libs/server-all.jar &
done
