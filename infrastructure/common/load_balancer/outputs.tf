output load_balancer_public_id {
  value = aws_instance.nginx.public_ip
}