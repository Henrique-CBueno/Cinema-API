provider "aws" {
  region                      = "us-east-1"
  access_key                  = "test"
  secret_key                  = "test"
  skip_credentials_validation = true
  skip_metadata_api_check     = true
  skip_requesting_account_id  = true

  endpoints {
    sns = "http://localhost:4566"
    sqs = "http://localhost:4566"
  }
}

# 1. Tópico SNS (O Fan-out)
resource "aws_sns_topic" "reservation_fanout" {
  name = "reservation-topic"
}

resource "aws_sns_topic" "notification_events" {
  name = "notification-topic"
}

# 2. Filas SQS
resource "aws_sqs_queue" "reservation_paid" {
  name = "reservation_paid"
}

resource "aws_sqs_queue" "reservation_notification" {
  name = "reservation_notification"
}

# 3. Assinaturas (Conectar SNS -> SQS)
resource "aws_sns_topic_subscription" "paid_subscription" {
  topic_arn = aws_sns_topic.reservation_fanout.arn
  protocol  = "sqs"
  endpoint  = aws_sqs_queue.reservation_paid.arn
  raw_message_delivery = true
}

resource "aws_sns_topic_subscription" "notification_subscription" {
  topic_arn = aws_sns_topic.reservation_fanout.arn
  protocol  = "sqs"
  endpoint  = aws_sqs_queue.reservation_notification.arn
  raw_message_delivery = true
}

resource "aws_sns_topic_subscription" "notification_event_subscription" {
  topic_arn = aws_sns_topic.notification_events.arn
  protocol  = "sqs"
  endpoint  = aws_sqs_queue.reservation_notification.arn
  raw_message_delivery = true
}

# 4. Políticas de Acesso (Permitir que o SNS envie para o SQS)
resource "aws_sqs_queue_policy" "sns_to_sqs_policy" {
  for_each = {
    paid = aws_sqs_queue.reservation_paid.id
    notif = aws_sqs_queue.reservation_notification.id
  }

  queue_url = each.value

  policy = <<POLICY
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": "*",
      "Action": "sqs:SendMessage",
      "Resource": "*",
      "Condition": {
        "ArnEquals": { "aws:SourceArn": ["${aws_sns_topic.reservation_fanout.arn}", "${aws_sns_topic.notification_events.arn}"] }
      }
    }
  ]
}
POLICY
}