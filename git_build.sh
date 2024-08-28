#!/bin/bash

if [ "$2" == "" ]; then
    echo usage: $0 \<Module\> \<Branch\> \<Workspace\> [\<BUILD_USER_ID\>] [\<D\>] [\<REASON\>]
    exit -1
else
    versionProperties=install/version.properties
    theDate=\#$(date +"%c")
    module=$1
    branch=$2
    PWD=$3
    BUILD_USER_ID=$4
    REASON=$5
    CT=/usr/atria/bin/cleartool
fi


function getReason {
        if [ -n "$REASON" ]; then
		REASON=`echo $REASON | sed 's/$\ /x/'`
		REASON=`echo JIRA:::$REASON | sed s/" "/,JIRA:::/g`
        else
                REASON="CI-DEV"
        fi
}


function getProductNumber {
        product=`cat $PWD/build.cfg | grep $module | grep $branch | awk -F " " '{print $3}'`
	tag_product=`echo $product | sed 's/\//_/g'`
}

function setRstate {
        revision=`cat $PWD/build.cfg | grep $module | grep " $branch " | awk -F " " '{print $4}'`

      if git tag | grep $product-$revision; then
            build_num=`git tag | grep $revision | wc -l`

            if [ "${build_num}" -lt 10 ]; then
				build_num=0$build_num
			fi
			rstate=`echo $revision$build_num | perl -nle 'sub nxt{$_=shift;$l=length$_;sprintf"%0${l}d",++$_}print $1.nxt($2) if/^(.*?)(\d+$)/';`
		else
            ammendment_level=01
            rstate=$revision$ammendment_level
        fi
}


function getSprint {
        sprint=`cat $PWD/build.cfg | grep $module | grep $branch | awk -F " " '{print $5}'`
}


function cleanup {
	if [ -f install/EniqEventsUI.war ] ; then
          echo "removing install/EniqEventsUI.war"
	  rm install/EniqEventsUI.war
	fi

	if [ -f install/version.properties ] ; then
       	  echo "Removing install/version.properties"
	  rm install/version.properties
	fi

	if [ -f *.zip ] ; then
	  ZIPFILE=`ls *.zip`
          echo "Removing ${ZIPFILE}"
	  rm ${ZIPFILE}
	fi
}

cleanup
getSprint
getProductNumber
setRstate
getReason
git clean -df
git checkout $branch
git pull


echo "Building for Sprint:$sprint"
echo "Building UI on $branch"
echo "Building rstate: $rstate"



mvn clean install -Dmaven.test.skip=true -P noPMD,eniq_events_release_git
rsp=$?

if [ $rsp == 0 ]; then
  git tag $tag_product-$rstate
  git pull
  git push --tag origin $branch

  touch $versionProperties
  echo $theDate >> $versionProperties
  echo module.name=EniqEventsUI >> $versionProperties
  echo module.version=$rstate >> $versionProperties
  echo build.tag=b999 >> $versionProperties
  echo author=$USER >> $versionProperties
  echo module.build=999 >> $versionProperties
  echo product.number=$product >> $versionProperties
  echo product.label=$tag_product-$rstate >> $versionProperties

  echo "Zipping all contents of the install directory..."
  zip -r eventsui_$rstate.zip install/*
  echo "Copying eventsui_$rstate.zip to /home/$USER/eniq_events_releases"
  cp eventsui_$rstate.zip /home/$USER/eniq_events_releases

  
if "${Deliver}"; then
    if [ "${DELIVERY_TYPE}" = "SPRINT" ]; then
    $CT setview -exec "/proj/eiffel013_config/fem101/jenkins_home/bin/lxb /vobs/dm_eniq/tools/scripts/deliver_eniq -auto events ${sprint} ${REASON} Y ${BUILD_USER_ID} ${product} NONE /home/$USER/eniq_events_releases/eventsui_$rstate.zip" deliver_ui
else
    $CT setview -exec "/proj/eiffel013_config/fem101/jenkins_home/bin/lxb /vobs/dm_eniq/tools/scripts/eu_deliver_eniq -EU events ${sprint} ${REASON} Y ${BUILD_USER_ID} ${product} NONE /home/$USER/eniq_events_releases/eventsui_$rstate.zip" deliver_ui
    fi
else
   echo "The delivery option was not selected.."
    fi
fi
exit $rsp
