resource "aws_instance" "nginx" {
  ami                    = var.server_ami
  instance_type          = var.server_instance_type
  vpc_security_group_ids = [ var.aws_security_group_id ]
  key_name               = var.aws_key_pair_name

  user_data = <<-EOF
    #!/bin/bash
    # Update packages and install NGINX
    yum update -y
    amazon-linux-extras enable nginx1
    yum install -y nginx

    # Configure NGINX as a load balancer
    cat <<EOT > /etc/nginx/conf.d/load-balancer.conf
    upstream web_server {
      ${join("\n  ", formatlist("server %s:8080;", var.servers))}
    }

    server {
      listen 80;
      location /api/v1 {
        proxy_pass              http://web_server;
        proxy_set_header        Host \$http_host;
        proxy_set_header        X-Real-IP \$remote_addr;
        proxy_set_header        X-Forwarded-For \$proxy_add_x_forwarded_for;
      }
    }
    EOT

    # Start and enable NGINX
    systemctl restart nginx
    systemctl enable nginx
  EOF

  tags = {
    Name = "NGINX Server"
  }
}
