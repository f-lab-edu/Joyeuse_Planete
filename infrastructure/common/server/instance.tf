resource "aws_instance" "server" {
  ami                    = var.server_ami
  instance_type          = var.server_instance_type
  count                  = var.server_count
  vpc_security_group_ids = [ var.aws_security_group_id ]
  key_name               = var.aws_key_pair_name

  user_data = <<-EOF
    #!/bin/bash
    # Update packages and install Docker
    yum update -y
    amazon-linux-extras enable docker
    yum install -y docker
    systemctl start docker
    systemctl enable docker

    # Run the Docker container
    docker run -d -p 8080:8080 --name web_server \
     -e KAFKA_SERVER_IP=${var.KAFKA_SERVER_IP}:9092 \
     -e DATABASE_URL=${var.DATABASE_URL} \
     -e DATABASE_USERNAME=${var.DATABASE_USERNAME} \
     -e DATABASE_PASSWORD=${var.DATABASE_PASSWORD} \
    ${var.docker_image}
  EOF

  tags = {
    Name = "Docker Server"
  }
}