start the jenkins container on docker desktop

export NGROK_AUTHTOKEN='paste-your-token-here'
export NGROK_DOMAIN='your-domain.ngrok-free.app'

docker run --rm -it --network jenkins_default \
  -e NGROK_AUTHTOKEN="$NGROK_AUTHTOKEN" \
  ngrok/ngrok:latest http --url="$NGROK_DOMAIN" jenkins:8080